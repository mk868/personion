package eu.softpol.app.personio;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtil {

  public static Duration parseDuration(String input) {
    Pattern pattern = Pattern.compile("(\\d+)h|(\\d+)m");
    Matcher matcher = pattern.matcher(input);

    long hours = 0;
    long minutes = 0;

    while (matcher.find()) {
      if (matcher.group(1) != null) {
        hours = Long.parseLong(matcher.group(1));
      }
      if (matcher.group(2) != null) {
        minutes = Long.parseLong(matcher.group(2));
      }
    }

    return Duration.ofHours(hours).plusMinutes(minutes);
  }

  public static String durationToString(Duration duration) {
    long totalMinutes = duration.toMinutes();
    long hours = totalMinutes / 60;
    long minutes = totalMinutes % 60;

    StringBuilder result = new StringBuilder();
    if (hours > 0) {
      result.append(hours).append("h");
    }
    if (minutes > 0) {
      if (!result.isEmpty()) {
        result.append(" ");
      }
      result.append(minutes).append("m");
    }

    if (result.isEmpty()) {
      result.append("0m");
    }

    return result.toString();
  }

  public static String durationToExcel(Duration duration) {
    long totalMinutes = duration.toMinutes();
    if (totalMinutes == 0) {
      return "0";
    }
    if (totalMinutes % 15 == 0) {
      return Double.toString(totalMinutes / 60.0);
    }
    // Excel formula
    var hourPart = duration.toHoursPart();
    var minutePart = duration.toMinutesPart();
    var result = "=";
    if (hourPart != 0) {
      result += hourPart + "+";
    }
    result += minutePart + "/60";
    return result;
  }

}
