package AndroidAppiumTests;

import io.appium.java_client.AppiumBy;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class ScrollDemo extends BaseTest {

    @Test
    public void ScrollDemoTest() throws MalformedURLException, InterruptedException {
        driver.findElement(AppiumBy.accessibilityId("Views")).click();

        // Method 1: Scroll directly to a known element text using Android UiScrollable
//        driver.findElement(AppiumBy.androidUIAutomator(
//                "new UiScrollable(new UiSelector()).scrollIntoView(text(\"WebView\"));"
//        ));

        // Method 2: Scroll down using W3C mobile: scrollGesture (when target text is unknown)
         scrollToEndAction();

        Thread.sleep(2000);
    }
}