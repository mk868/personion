package eu.softpol.app.personio.page;

import org.openqa.selenium.By;

public class TimeTrackingPage {

  public static final By DAY_HEADER_ELEMENT = By.cssSelector(
      "[class^='TimecardRow-module__timecard__']");
  public static final By DAY_DATE_ELEMENT = By.cssSelector(
      "[class^='TimecardRow-module__dayCell__'] time");
  public static final By DAY_TRACKED_VS_TARGET_ELEMENT = By.cssSelector(
      "[data-test-id='tracked-vs-target-area']");

  public static final By ENTRY_ELEMENT = By.cssSelector(
      "[class^='TimePeriodRow-module__timePeriodRow__']");
  public static final By ENTRY_TIME_ELEMENT = By.cssSelector(
      "[class^='TimePeriodsInput-module__time__']");
  public static final By ENTRY_PROJECT_ELEMENT = By.cssSelector(
      "button[data-test-id='time-period-row-project-picker-trigger']");
  public static final By ENTRY_COMMENT_ELEMENT = By.cssSelector(
      "input[data-test-id^='timecard-'][data-test-id$='-comment']");

}
