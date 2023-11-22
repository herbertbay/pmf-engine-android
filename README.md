# pmf-engine-android
pmf_engine_android is an Android framework that empowers developers to seamlessly integrate interactive question prompts for valuable user feedback, ensuring two weeks of app usage and tracking at least two key events to ensure meaningful insights and engagement.

Requirements

Android min SDK 24

Configuration

1. To get started, import the pmf_engine_android package into your project.

import pmf_engine_android

2. In your MainActivity  override fun onCreate(savedInstanceState: Bundle?) method, configure the PMF Engine with your accountId and a unique userId.

  PMFEngine.default.configure("accountID", UUID().uuidString)
  
3. Track Events

Use the PMF Engine to track events within your application. You can either use a default event or specify a custom event.

  PMFEngine.default.trackKeyEvent() // Track a default event
  PMFEngine.default.trackKeyEvent("journal") // Track a custom event

4. Show the Feedback Popup

To show the form directly from the top controller:

  PMFEngine.default.showPMFPopup()

License

pmf_engine_android is available under the MIT license. See the LICENSE file for more info.
