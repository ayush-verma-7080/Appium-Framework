package AndroidAppiumTests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.DeviceRotation;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

public class MiscellaneousAppiumActions extends BaseTest {

    @Test
    public void Miscellaneous() throws MalformedURLException {
        // 1. Navigate to Preference -> 3. Preference dependencies -> check WiFi checkbox
        driver.findElement(AppiumBy.accessibilityId("Preference")).click();
        driver.findElement(By.xpath("//android.widget.TextView[@content-desc='3. Preference dependencies']")).click();
        driver.findElement(By.id("android:id/checkbox")).click();

        // 2. Rotate screen to Landscape (90 degrees on Z-axis)
        DeviceRotation landScape = new DeviceRotation(0, 0, 90);
        driver.rotate(landScape);

        // 3. Open WiFi settings dialog & assert title
        driver.findElement(By.xpath("(//android.widget.RelativeLayout)[2]")).click();
        String alertTitle = driver.findElement(By.id("android:id/alertTitle")).getText();
        Assert.assertEquals(alertTitle, "WiFi settings");

        // 4. Copy text to device clipboard and paste it into the input box
        driver.setClipboardText("Rahul Wifi");
        driver.findElement(By.id("android:id/edit")).sendKeys(driver.getClipboardText());

        // 5. Press Android keys (ENTER, OK button, BACK, HOME)
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        driver.findElements(AppiumBy.className("android.widget.Button")).get(1).click();
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
        driver.pressKey(new KeyEvent(AndroidKey.HOME));
    }
}