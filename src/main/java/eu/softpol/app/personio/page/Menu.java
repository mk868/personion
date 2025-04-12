package eu.softpol.app.personio.page;

import org.openqa.selenium.By;

public class Menu {

  public static final By MENU_ITEM_HOME_ELEMENT = By.cssSelector(
      "[data-test-id='navsidebar-dashboard']");
  public static final By MENU_ITEM_TIME_TRACKING_ELEMENT = By.cssSelector(
      "[data-test-id='navsidebar-timesheet']");
}
