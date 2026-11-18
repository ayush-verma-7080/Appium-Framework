package AndroidAppiumTests;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    public AppiumDriverLocalService service;
    public AndroidDriver driver;

    @BeforeClass
    public void configureAppiumSetup() {
        String userHome = System.getProperty("user.home"); // Resolves to C:\Users\Administrator

        // 1. Point ANDROID_HOME to your SDK path
        // (If Android SDK is in C:\Users\Administrator\AppData\Local\Android\Sdk, keep this;
        //  if you moved it to D:\, update sdkPath accordingly)
        String sdkPath = userHome + "\\AppData\\Local\\Android\\Sdk";
        Map<String, String> env = new HashMap<>(System.getenv());
        env.put("ANDROID_HOME", sdkPath);
        env.put("ANDROID_SDK_ROOT", sdkPath);

        // 2. Point to Appium's main.js under Administrator (or global npm path)
        File appiumMainJs = new File(userHome + "\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js");

        service = new AppiumServiceBuilder()
                .withAppiumJS(appiumMainJs)
                .withIPAddress("127.0.0.1")
                .usingAnyFreePort()
                .withEnvironment(env)
                .withTimeout(Duration.ofSeconds(60))
                .build();

        service.start();

        // 3. Point to your restored ApiDemos-debug.apk path (update if stored on D:\)
        String apkPath = userHome + "\\Downloads\\APKFiles\\resources\\ApiDemos-debug.apk";

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setDeviceName("RZCT30ZX37H")
                .setUdid("RZCT30ZX37H")
                .setApp(apkPath)
                .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(60))
                .setNoReset(true);

        driver = new AndroidDriver(service.getUrl(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAppiumSetup() {
        if (driver != null) {
            driver.quit();
        }
        if (service != null && service.isRunning()) {
            service.stop();
        }
    }

    public void longPressAction(WebElement ele) {
        Assert.assertNotNull(((RemoteWebElement) ele).getId());
        ((JavascriptExecutor) driver).executeScript("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) ele).getId(),
                "duration", 2000
        ));
    }

    public void scrollToEndAction() {
        boolean canScrollMore;
        do {
            canScrollMore = (Boolean) ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture",
                    ImmutableMap.of("left", 100, "top", 100, "width", 200, "height", 200, "direction", "down", "percent", 3.0));
        } while (canScrollMore);
    }

    public void swipeAction(WebElement ele, String direction) {
        Assert.assertNotNull(((RemoteWebElement) ele).getId());
        ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) ele).getId(),

                "direction", direction,
                "percent", 0.75
        ));
    }

    public Double getFormattedAmount(String amount) {
        Double price = Double.parseDouble(amount.substring(1));
        return price;
    }
}