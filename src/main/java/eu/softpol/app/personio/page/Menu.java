package eu.softpol.app.personio.page;

import org.openqa.selenium.By;

public class Menu {

  public static final By MENU_ITEM_HOME_ELEMENT = By.cssSelector(
      "[data-test-id='navsidebar-dashboard']");
  public static final By MENU_ITEM_TIME_OFF_AND_ATTENDANCE = By.cssSelector(
      "[data-test-id='navsidebar-companyTimeline']");
}
