```markdown
# Mobile Automation Framework — Knowledge Transfer (KT) & Architecture Guide

## 1. Overview & Architecture
This repository contains an **Appium 2.x** mobile test automation framework built in **Java** using **TestNG** and the **W3C WebDriver protocol** (`io.appium:java-client`). It automates native Android applications (`ApiDemos-debug.apk` and `General-Store.apk`) on a physical Android device (**Samsung Galaxy M32**, IP `192.168.31.91`) over Wireless ADB.

### Client–Server Execution Flow
1. **Test Client (Java + TestNG):** Test classes extend `BaseTest` and send W3C WebDriver HTTP/JSON commands via `AndroidDriver`.
2. **Appium 2 Server (Node.js):** Started programmatically inside `@BeforeClass` via `AppiumDriverLocalService` on `127.0.0.1:4723`, translating W3C payloads into native Android commands using the **UiAutomator2** driver.
3. **Device Execution (ADB + UiAutomator2):** Connects to the Samsung Galaxy M32 over Wi-Fi (`adb connect 192.168.31.91:<PORT>`), installs the target `.apk`, and executes native touch gestures, key events, and UI assertions.

---

## 2. Environment & Prerequisites
* **OS:** Windows 11
* **Java / IDE:** JDK + IntelliJ IDEA (Maven / Gradle)
* **Android SDK & Platform Tools:** Installed at `C:\Android\Sdk` (`ANDROID_HOME` & `ANDROID_SDK_ROOT` configured in system environment variables and injected into the Appium server process).
* **Node.js & Appium 2:** Appium server installed globally via `npm` (`%APPDATA%\npm\node_modules\appium\build\lib\main.js`) with the `uiautomator2` driver installed.
* **Target Device:** Samsung Galaxy M32 (`192.168.31.91`) with **Developer Options** and **Wireless Debugging** enabled.

### Wireless ADB Connection Steps (Before Running Tests)
```cmd
# 1. Pair device (only required once or if pairing resets)
C:\Android\Sdk\platform-tools\adb.exe pair 192.168.31.91:<PAIRING_PORT> <6_DIGIT_CODE>

# 2. Connect using the main Wireless Debugging port
C:\Android\Sdk\platform-tools\adb.exe connect 192.168.31.91:<MAIN_PORT>

# 3. Verify active device connection
C:\Android\Sdk\platform-tools\adb.exe devices

```

> **Note:** Update `.setDeviceName("192.168.31.91:<MAIN_PORT>")` and `.setUdid("192.168.31.91:<MAIN_PORT>")` in `BaseTest.java` whenever the wireless debugging port changes.

---

## 3. Project Structure

```text
src/test/java/AndroidAppiumTests/
├── BaseTest.java                    # Appium server lifecycle, driver setup & W3C gesture helpers
├── AppiumBasics.java                # Basic locators & WiFi settings dialog flow (API Demos)
├── LongPress.java                   # W3C long-press gesture validation (API Demos)
├── ScrollDemo.java                  # Native UiScrollable & W3C scrollGesture demos (API Demos)
├── SwipeDemo.java                   # Horizontal carousel swipe & attribute validation (API Demos)
├── DragDropDemo.java                # W3C drag-and-drop gesture validation (API Demos)
├── MiscellanousAppiumActions.java   # Screen rotation, Clipboard & Android KeyEvents (API Demos)
├── eCommerce_tc_1.java              # Form validation & Android Toast error verification (General Store)
├── eCommerce_tc_2.java              # Dynamic product search, cart addition & explicit waits (General Store)
└── eCommerce_tc_3.java              # End-to-end cart total calculation, long press & checkout (General Store)

```

---

## 4. Core Framework Components (`BaseTest.java`)

### Lifecycle Hooks

* **`@BeforeClass` (`ConfigureAppium`)**:
* Injects `ANDROID_HOME` and `ANDROID_SDK_ROOT` (`C:\Android\Sdk`) into the Appium server environment map.
* Starts `AppiumDriverLocalService` programmatically on `127.0.0.1:4723`.
* Configures `UiAutomator2Options` (`deviceName`, `udid`, `app`) and initializes `AndroidDriver` with a `10s` implicit wait.


* **`@AfterClass` (`tearDown`)**:
* Quits the `AndroidDriver` session and stops `AppiumDriverLocalService` to release port `4723`.



### Reusable Utility Methods

All touch gestures use Appium 2 W3C `mobile:*` commands via `JavascriptExecutor` and `ImmutableMap` (replacing deprecated `TouchAction` APIs):
| Helper Method | Full Command / Implementation | Purpose |
| :--- | :--- | :--- |
| `longPressAction(WebElement ele)` | `((JavascriptExecutor) driver).executeScript("mobile: longClickGesture", ImmutableMap.of("elementId", ((RemoteWebElement) ele).getId(), "duration", 2000));` | Press and hold an element for 2 seconds to trigger context menus or popups. |
| `scrollToText(String text)` | `driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + text + "\"));"));` | Scroll natively until an element matching `text` enters the viewport. |
| `scrollToEndAction()` | `do { canScrollMore = (Boolean) ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", ImmutableMap.of("left", 100, "top", 100, "width", 200, "height", 200, "direction", "down", "percent", 3.0)); } while (canScrollMore);` | Scroll down iteratively until the bottom edge of a container is reached. |
| `swipeAction(WebElement ele, String direction)` | `((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of("elementId", ((RemoteWebElement) ele).getId(), "direction", direction, "percent", 0.75));` | Swipe across 75% of an element's bounds in the specified direction (`left`, `right`, `up`, `down`). |
| `dragDropAction(WebElement source, int endX, int endY)` | `((JavascriptExecutor) driver).executeScript("mobile: dragGesture", ImmutableMap.of("elementId", ((RemoteWebElement) source).getId(), "endX", endX, "endY", endY));` | Drag a source element and drop it at target `(endX, endY)` coordinates. |
| `getFormattedAmount(String amount)` | `Double.parseDouble(amount.substring(1));` | Strip leading currency symbol (`$`) and parse price strings into `Double`. |
---

## 5. Test Suite Coverage

### Part A: API Demos Test Suite (`ApiDemos-debug.apk`)

1. **`AppiumBasics.java`**: Navigates **Preference → 3. Preference dependencies**, checks the WiFi checkbox, enters `"Rahul Wifi"`, asserts dialog title `"WiFi settings"`, and clicks **OK** using `accessibilityId`, `xpath`, `id`, and `className`.
2. **`LongPress.java`**: Navigates **Views → Expandable Lists → 1. Custom Adapter**, long-presses `"People Names"` for 2 seconds, and asserts `"Sample menu"` is displayed.
3. **`ScrollDemo.java`**: Navigates to **Views** and demonstrates scrolling directly to `"WebView"` via `UiScrollable` as well as coordinate-based `mobile: scrollGesture`.
4. **`SwipeDemo.java`**: Navigates **Views → Gallery → 1. Photos**, verifies the first `ImageView` has `focusable="true"`, swipes left, and asserts `focusable` flips to `"false"`.
5. **`DragDropDemo.java`**: Navigates **Views → Drag and Drop**, drags `drag_dot_1` onto the target zone, and asserts `"Dropped!"`.
6. **`MiscellanousAppiumActions.java`**: Rotates the screen to Landscape (`DeviceRotation(0, 0, 90)`), writes/reads from the OS clipboard (`setClipboardText` / `getClipboardText`), and triggers hardware keys (`AndroidKey.ENTER`, `BACK`, `HOME`).

### Part B: General Store E-Commerce Test Suite (`General-Store.apk`)

> **Prerequisite:** Update `options.setApp(...)` in `BaseTest.java` to point to `General-Store.apk` before running these tests.

1. **`eCommerce_tc_1.java` (Form & Toast Validation)**:
* **Negative Flow (`FillForm_ErrorValidation`)**: Submits the form with an empty name field and captures the transient Android Toast message via `(//android.widget.Toast)[1].getAttribute("name")`, asserting `"Please enter your name"`.
* **Positive Flow (`FillForm_PositiveFlow`)**: Enters `"Rahul Shetty"`, hides keyboard, selects `"Female"`, scrolls the country dropdown to `"Argentina"`, and clicks **Let's Shop**.


2. **`eCommerce_tc_2.java` (Product Search & Cart Verification)**:
* Scrolls dynamically to `"Jordan 6 Rings"`, matches its index among visible product cards to click the corresponding **ADD TO CART** button, waits for the **Cart** toolbar using `WebDriverWait` (`ExpectedConditions.attributeContains`), and verifies `"Jordan 6 Rings"` is present in the cart.


3. **`eCommerce_tc_3.java` (Cart Total Calculation & Checkout)**:
* Adds the first two products to the cart, parses and sums their prices using `getFormattedAmount()`, asserts the computed sum equals `totalAmountLbl`, long-presses **Terms and Conditions**, closes the popup, checks the agreement checkbox, and clicks **Proceed**.



---

## 6. Key Best Practices & Troubleshooting Notes

* **Dynamic Index Shifting on "ADD TO CART"**: When adding multiple sequential items in `eCommerce_tc_3`, clicking the first `//android.widget.TextView[@text='ADD TO CART']` changes its label to `ADDED TO CART`, shifting the next item to index `0`.
* **Toast Message Locators**: Android Toast popups cannot be inspected normally in Appium Inspector; always locate them with `By.xpath("(//android.widget.Toast)[1]")` and read their text via `.getAttribute("name")`.
* **Explicit Waits for Page Transitions**: When navigating between screens sharing identical element IDs (such as `productName` on the catalog screen vs. the cart screen), use `WebDriverWait` on `toolbar_title` before querying elements to prevent stale or mismatched assertions.
* **Screen Resolution Independence**: For `DragDropDemo` across different device resolutions, compute drop coordinates dynamically from the target element's center (`target.getLocation()` + half width/height) instead of hardcoded pixel values.

```

```