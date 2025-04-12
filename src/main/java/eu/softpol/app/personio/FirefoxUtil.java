package eu.softpol.app.personio;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxUtil {

  public static void createFirefoxProfile(File profileDir) throws IOException {
    // create a new empty profile
    var profile = new FirefoxProfile();
    profile.setAcceptUntrustedCertificates(true);
    profile.setAssumeUntrustedCertificateIssuer(false);
    profile.setPreference("browser.startup.homepage", "about:blank");
    profile.setPreference("startup.homepage_welcome_url", "about:blank");
    profile.setPreference("startup.homepage_welcome_url.additional", "about:blank");
    profile.setPreference("network.cookie.cookieBehavior", 0);
    profile.setPreference("network.cookie.lifetimePolicy", 0);
    profile.setPreference("signon.rememberSignons", true);
    profile.setPreference("browser.cache.disk.enable", true);
    profile.setPreference("browser.cache.memory.enable", true);
    profile.setPreference("browser.sessionstore.resume_from_crash", true);
    profile.setPreference("browser.sessionstore.max_resumed_crashes", -1);
    profile.setPreference("browser.startup.page", 3);
    profile.setPreference("browser.tabs.warnOnClose", false);
    profile.setPreference("privacy.clearOnShutdown.cookies", false);
    profile.setPreference("privacy.clearOnShutdown.history", false);
    profile.setPreference("privacy.clearOnShutdown.formdata", false);
    profile.setPreference("privacy.clearOnShutdown.passwords", false);
    profile.setPreference("privacy.clearOnShutdown.sessions", false);
    profile.setPreference("privacy.sanitize.sanitizeOnShutdown", false);
    profile.setPreference("services.sync.prefs.sync.browser.sessionstore.resume_session_once",
        true);
    var tmpLocation = profile.layoutOnDisk();
    FileUtils.copyDirectory(tmpLocation, profileDir);
  }
}
