package eu.softpol.app.personio;

public class JiraUtil {

  public static boolean isJiraKey(String input) {
    return input.matches("^[A-Z0-9]+-\\d+$");
  }
}
