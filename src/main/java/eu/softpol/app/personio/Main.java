package eu.softpol.app.personio;

import static eu.softpol.app.personio.DurationUtil.durationToExcel;
import static eu.softpol.app.personio.DurationUtil.durationToString;
import static eu.softpol.app.personio.page.TimeTrackingPage.DAY_DATE_ELEMENT;
import static eu.softpol.app.personio.page.TimeTrackingPage.DAY_TRACKED_VS_TARGET_ELEMENT;
import static org.openqa.selenium.support.ui.ExpectedConditions.numberOfElementsToBeMoreThan;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import eu.softpol.app.personio.page.HomePage;
import eu.softpol.app.personio.page.Menu;
import eu.softpol.app.personio.page.TimeTrackingPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    var profileDir = new File("firefox-profile").getAbsoluteFile();
    var rawOutputFile = new File("raw-" + LocalDate.now().getMonthValue() + ".txt");
    var summaryOutputFile = new File("summary-" + LocalDate.now().getMonthValue() + ".txt");
    if (!profileDir.exists()) {
      FirefoxUtil.createFirefoxProfile(profileDir);
    }
    WebDriverManager.firefoxdriver().setup();
    var options = new FirefoxOptions();
    options.addArguments("-profile", profileDir.toString());
    var driver = new FirefoxDriver(options);
    AutoCloseable closeDirver = driver::quit;
    try (closeDirver;
        var rawOutput = new TextOutput(rawOutputFile);
        var summaryOutput = new TextOutput(summaryOutputFile);
    ) {
      driver.get("https://gcs.personio.de");

      var wait = new FluentWait<>(driver);
      wait.withTimeout(Duration.ofMinutes(2));
      Thread.sleep(5000);
      wait.until(visibilityOfElementLocated(HomePage.ANY_DASHBOARD_ELEMENT));
      log.info("Dashboard is visible, user is logged in");
      wait.withTimeout(Duration.ofSeconds(10));

      var menuTimeTracking = wait.until(
          visibilityOfElementLocated(Menu.MENU_ITEM_TIME_TRACKING_ELEMENT));

      menuTimeTracking.click();

      var days = wait.until(numberOfElementsToBeMoreThan(TimeTrackingPage.DAY_HEADER_ELEMENT, 10));
      var noOfDays = days.size();

      for (var i = 0; i < noOfDays; i++) {
        var day = days.get(i);

        var date = day.findElement(DAY_DATE_ELEMENT)
            .getText();
        var trackedVsTargetOpt = day.findElements(DAY_TRACKED_VS_TARGET_ELEMENT).stream()
            .map(WebElement::getText)
            .findFirst();
        log.info("Date: {}, Tracked vs Target: {}", date, trackedVsTargetOpt);

        if (trackedVsTargetOpt.isEmpty()) {
          log.info("Skipping empty day");
          continue;
        }
        var trackedVsTarget = trackedVsTargetOpt.get();
        if (trackedVsTarget.equals("0h/8h")) {
          log.info("Skipping not filled day");
          continue;
        }

        rawOutput.write("Date: %s, Tracked vs Target: %s".formatted(date, trackedVsTarget));
        summaryOutput.write("Date: %s, Tracked vs Target: %s".formatted(date, trackedVsTarget));

        day.click();
        Thread.sleep(1000);
        days = wait.until(numberOfElementsToBeMoreThan(TimeTrackingPage.DAY_HEADER_ELEMENT, 10));
        day = days.get(i);

        var normalizedEntries = new ArrayList<Entry>();
        var entries = driver.findElements(TimeTrackingPage.ENTRY_ELEMENT);
        log.info("Found {} entries", entries.size());
        for (var entry : entries) {
          var type = entry.findElement(TimeTrackingPage.ENTRY_TYPE_ELEMENT)
              .getDomAttribute("value");
          if (!type.equals("work")) {
            log.info("Skipping non-work entry");
            continue;
          }
          var time = entry.findElement(TimeTrackingPage.ENTRY_TIME_ELEMENT).getText();
          if (time.equals("0h")) {
            log.info("Skipping empty entry");
            continue;
          }
          var project = entry.findElement(TimeTrackingPage.ENTRY_PROJECT_ELEMENT).getText();
          var comment = entry.findElement(TimeTrackingPage.ENTRY_COMMENT_ELEMENT)
              .getDomProperty("value");
          log.info("Time: {}, Project: {}, Comment: {}", time, project, comment);

          rawOutput.write(1, "Time: %s, Project: %s, Comment: %s"
              .formatted(time, project, comment));
          normalizedEntries.add(new Entry(time, project, comment));
        }

        // post process normalized entries
        var projectToEntries = normalizedEntries.stream()
            .collect(Collectors.groupingBy(Entry::project));
        for (var kv : projectToEntries.entrySet()) {
          var projectName = kv.getKey();
          summaryOutput.write(1, projectName);

          var taskToEntries = kv.getValue().stream()
              .collect(Collectors.groupingBy(e ->
                  Optional.of(e.comment())
                      .map(c -> {
                        if (c.contains(";")) {
                          return c.substring(0, c.indexOf(";")).trim();
                        }
                        return c;
                      })));
          for (var kv2 : taskToEntries.entrySet()) {
            var task = kv2.getKey();
            var timePerKey = kv2.getValue().stream()
                .map(Entry::time)
                .map(DurationUtil::parseDuration)
                .reduce(Duration.ZERO, Duration::plus);
            var mergedCommentsForJira = kv2.getValue().stream()
                .map(Entry::comment)
                .filter(c -> c.contains(";"))
                .map(c -> c.substring(c.indexOf(";") + 1).trim())
                .collect(Collectors.joining(","));

            summaryOutput.write(2, "%s: %s  [ %s ]   %s".formatted(
                task,
                durationToString(timePerKey),
                durationToExcel(timePerKey),
                mergedCommentsForJira.isEmpty() ? "" : "\"" + mergedCommentsForJira + "\""
            ));
          }
        }
      }

      Thread.sleep(1_000);
    }
  }
}
