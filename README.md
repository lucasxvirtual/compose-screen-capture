# compose-screen-capture
Compose Screen Capture is an Android library designed to simplify the process of capturing screenshots of layouts created with Jetpack Compose.

## Key Features:

*   **Seamless Integration with Jetpack Compose:**
    *   Designed to seamlessly work with the declarative design model of Jetpack Compose.
*   **Capture Complete Layouts:**
    *   Capture screenshots of entire layouts, providing a comprehensive view of the UI design.

## Setup

1.  Add JitPack repository to your project `build.gradle` file:

    ```
    allprojects {
       repositories {
          maven { url 'https://jitpack.io' }
       }
    }
    ```
   
2.  Add the dependency in your app `build.gradle` file:
    
    `implementation 'com.github.lucasxvirtual:compose-screen-capture:1.0.0'`

## Usage
    
1.  Configure the screenshot capture in your Compose code:
    
    ```kotlin
    val screenCaptureState = rememberScreenCaptureState()     //provides a ScreenCaptureState
    ScreenCapture(screenCaptureState = screenCaptureState) {
      YourComposeLayout()
    }
    ```

2.  Call `screenCaptureState.capture()` when ready to take the screen capture:

    ```kotlin
    Button(
       onClick = {
          screenCaptureState.capture() 
       }
    ) {
       Text(text = "Take screen capture")
    }
    ```
3.  Retrieve the captured image or error:

    ```kotlin
    when (screenCaptureState.imageState) {
       is ScreenShotResult.Success -> { screenCaptureState.bitmap }
       is ScreenShotResult.Error -> { ... }
    }
    
    ----------------- OR -----------------
    
    screenCaptureState.bitmap?.let { ... }
    (screenCaptureState.imageState as? ScreenShotResult.Error)?.let { ... }
    ```

4.  (Optional) When working with scrollable screens that need to fit in one print you can calculate the size of the screen and pass in the `screenCaptureState.capture(options: ScreenCaptureOptions?)` function **(make sure to correctly calculate the necessary size in order to reduce memory usage)**

    ```kotlin
    Button(
       onClick = {
          screenCaptureState.capture(
             options = ScreenCaptureOptions(height = localView.measuredHeight * 4)     //estimated size, pass the closest calculation you have
          ) 
       }
    ) {
       Text(text = "Take screen capture")
    }
    ```

## Contribution
Contributions are welcome! Feel free to open issues, submit pull requests, or suggest improvements.

## License
This library is distributed under the Apache-2.0 License. See the `LICENSE` file for details.

**Enjoy Simplified Screen Capture with Compose Screen Capture!**
