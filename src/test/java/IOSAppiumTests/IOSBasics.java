package IOSAppiumTests;

import io.appium.java_client.AppiumBy;
import org.testng.annotations.Test;

public class IOSBasics extends IOSBaseTest {

    @Test
    public void IOSBasicsTest() {
        // iOS Locators: Xpath, classname, IOS, iosClassChain, IOSPredicateString, accessibility id, id
        driver.findElement(AppiumBy.accessibilityId("Alert Views")).click();

        // XPath (slower on iOS because it parses the entire XML app source)
        // driver.findElement(By.xpath("//XCUIElementTypeStaticText[@name='Text Entry']")).click();

        // iOS Class Chain (fast alternative to XPath for iOS)
        driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`label == 'Text Entry'`]")).click();
        driver.findElement(AppiumBy.iOSClassChain("**/XCUIElementTypeCell")).sendKeys("Hello World");
        driver.findElement(AppiumBy.accessibilityId("OK")).click();

        // iOS NS Predicate String (supports exact match, BEGINSWITH, ENDSWITH, CONTAINS, and [c] case-insensitivity)
        driver.findElement(AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value == 'Confirm / Cancel'"));
        driver.findElement(AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value BEGINSWITH[c] 'Confirm'"));
        driver.findElement(AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeStaticText' AND value ENDSWITH[c] 'Cancel'"));
    }
}
