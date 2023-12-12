# compose-screen-capture
ComposeScreenshot is an Android library designed to simplify the process of capturing screenshots of layouts created with Jetpack Compose.

**Key Features:**

*   **Seamless Integration with Jetpack Compose:**
    *   Designed to seamlessly work with the declarative design model of Jetpack Compose.
*   **Capture Complete Layouts:**
    *   Capture screenshots of entire layouts, providing a comprehensive view of the UI design.

**How to Use:**

1.  Add the dependency in your `build.gradle` file:
    
    `implementation 'com.github.lucasxvirtual:compose-screen-capture:1.0.0'`
    
2.  Configure the screenshot capture in your Compose code:
    
    ```
    ScreenCapture(
      onResult = { result ->
        takeScreenCapture.value = false
        when (result) {
          is ScreenShotResult.Success -> {
            val myBitmap = result.bitmap
            ...
          }
          is ScreenShotResult.Error -> { ... }
        }
      },
      takeScreenCapture = takeScreenCapture, // takeScreenCapture: MutableState<Boolean>
      options = ScreenCaptureOptions(height = composeLayoutHeight, width = composeLayoutWidth) // optional if working with layouts that have scroll
    ) {
      YourComposeLayout()
    }
    ```

**Contribution:** Contributions are welcome! Feel free to open issues, submit pull requests, or suggest improvements.

**License:** This library is distributed under the APACHE 2.0 License. See the `LICENSE` file for details.

**Enjoy Simplified Screen Capture with Compose Screen Capture!**
