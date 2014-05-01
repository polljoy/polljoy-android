![Picture](http://www.polljoy.com/assets/images/logo/logo.png)
> In-app polls made easy. Integrate in 2 lines of code.

#Polljoy Android Integration Guide

Welcome friend! This guide will get you started with polljoy, fast & easy.

Got questions?  Email us at help@polljoy.com

-
<b>Simple</b> ??polljoy is designed to be simple for users and especially developers. Just 2 API calls, you can get your polls running.

<b>Open</b> -The polljoy API is open. The SDK comes with all source code and a test app as well as a compiled iOS framework ??`Polljoy.framework` and the resource bundle ??`Polljoy.bundle`. You can simply install the SDK as-is to integrate the polljoy service.

<b>Easy</b> ??polljoy is easy to use. Check out the test App in the SDK. Test with your own user id and app id. You can see how polljoy works. 

<b>Flexible</b> ??the polljoy SDK comes with the required UI to present the poll and do all the tasks for you. But if you want to implement your own UI, you can. The poll data is open. Enjoy!


# The polljoy Admin Console
You can setup and manage all your polls through a web interface here https://admin.polljoy.com

Note: Please note - PollJoy requires Android SDK level 8 (Android 2.2) or later.  

# Setup your XCode Project

1. Unzip the polljoy framework to your local drive
2. Drag the `Polljoy.framework` & `Polljoy.bundle` to your project in Xcode 5
3. Add the following iOS frameworks to link
  * UIKit
  * Foundation
  * CoreGraphics

  ### How To:
  i. Click on your XCode project in the file browser sidebar
  ii. Go to your XCode project?™s `Build Phases` tab
  iii. Expand `"Link Binary With Libraries"`
  iv. Click on the `+` button and add the frameworks listed above
  v. Check if `Polljoy.framework` is added. If not, add `Polljoy.framework` from your file browser as well.
  
  ![Picture](Doc/framework.png)

  ### Configure polljoy
  #### Setup Linker Flags
  1. Click on your Xcode projet in the file navigator sidebar
  2. Go to your XCode project?™s `Build Settings` tab
  3. Search for `Other Linker Flags`
  4. Double click in the blank area to the right of `Other Linker Flags` but under the ?œYes??of `Link With Standard Libraries`
  5. Add the following:
    `-ObjC`
  
  ![Picture](Doc/linker_flag.png)

  #### Add polljoy resources
  1. Go back to your Xcode project?™s `Build Phases` tab
  2. Expand `Copy Bundle Resources`
  3. Drag `Polljoy.bundle` in file navigator into `Copy Bundle Resources`
  
  ![Picture] (Doc/bundle.png)

### Implement polljoy in Project

 polljoy works in the background to avoid interruption to your app?™s main thread.
 
 polljoy requires each app to register a session and obtain the **Session ID** for all communications to the API. To have best performance and integration, we recommend registering the session at application startup. You?™ll need your **App ID** (they are available in the web [admin panel](https://admin.polljoy.com)
 
 To register a session:
 1. Open up your app?™s `AppDelegate.m` file
 2. Under `#import "AppDelegate.h"`, import `<Polljoy/Polljoy.h>` file
 3. Under implementation, call `Polljoy` to start session with your **App ID**
 
 ``` objective-c
 #import <Polljoy/Polljoy.h>

 // ...
 - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
     // ...
     [Polljoy startSession:@"YOUR_APP_ID"];
     // ...
 }
 ```
 
 polljoy SDK will automatically handle all session control and all required information to get the correct poll based on your poll setup in admin panel and save your poll result for analysis. These includes the session ID, session count, time (days) since first call polljoy SDK, device ID, platform, OS version ??etc. 

 Each time you call `startSession`, SDK will increase the session count by 1. So, you should only call it once for each app launch to get the session count correct.
 
 Once the session is started, SDK will cache all app settings including the default image (if any) that you have setup in the [admin panel](https://admin.polljoy.com). After caching, there will be no operation until you request polls from polljoy service.

### Get polls

After you started the session, you can get polls at any time and place you want!

In your program logic, import `<Polljoy/Polljoy.h>` at the program you want to get polls (or you can import in your `.pch` file). Then call:

 ``` objective-c
 // ...
   [Polljoy getPollWithDelegate:self
                     AppVersion:_appVersion
                          level:_level
                       userType:_userType];
 // ...
 ```
  
In summary:

`delegate`: the instance to handle all callbacks from polljoy SDK. The delegate should conform to `PolljoyDelegate` as defined in `Polljoy.h`

`appVersion`: your app?™s version to be used as a poll selection criteria. This should match with your poll setting. Or set it as nil if you are not using.

`Level`: if your app is a game app, this is your game level. This should match with your poll setting. Or set it as 0 if you are not using.

`userType`: your app user type either **Pay** or **Non-Pay**. This is the `ENUM PJUserType` as defined in `Polljoy.h`

Please check `Polljoy.h` for the type of the parameters. polljoy?™s API is open. All data returned is passed back to the delegate. Delegate can use the returned poll data for their own control if needed.

`NOTE: if you don?™t use any poll selection criteria, you can simply call the following method and let the SDK handle everything.

  ``` objective-c
  // if you DON?™T need to handle callbacks from Polljoy
  // this will auto show the polls when all polls are ready
      [Polljoy getPoll];
  // ...
  ```
  OR
  ``` objective-c
  // if you need to handle callbacks from Polljoy
    [Polljoy getPollWithDelegate:self];
  // ..
  ```
  
### Handle callbacks from SDK

polljoy will inform delegate at different stages when polls are downloaded, ready to show, user responded etc. App can optionally implement the delegate methods to control the app logic. The delegate methods are:

 ``` objective-c
 -(void) PJPollNotAvailable:(PJResponseStatus) status;
 ```
 
When there is no poll match with your selection criteria or no more polls to show in the current session. 

 ``` objective-c
 -(void) PJPollIsReady:(NSArray *) polls;
 ```
 
After you request for poll and poll/s is/are ready to show (including all defined images are downloaded). Friendly tip - If you are displaying the poll in the middle of an active game or app session that needs real time control, consider to pause your app before presenting the poll UI as needed. 

polls array returned are all the matched polls for the request. Please refer `PJPoll.h` for the data structure.
When you?™re ready to present the poll, call:

 ``` objective-c
 [Polljoy showPoll];
 ```

This will present the polljoy UI according to your app color and poll settings. Then polljoy SDK will handle all the remaining tasks for you. These include handling the user?™s response, informing delegate for any virtual amount user received, upload result to polljoy service ??etc.

We highly recommend you implement this delegate method so that you know polls are ready and call polljoy SDK to show the poll or do whatever control you need.

 ``` objective-c
 -(void) PJPollWillShow:(PJPoll*) poll;
 ```
 
The polljoy poll UI is ready and will show. You can do whatever UI control as needed. Or simply ignore this implementation.

 ``` objective-c
 -(void) PJPollDidShow:(PJPoll*) poll;
 ```
 
The polljoy poll UI is ready and has shown. You can do whatever UI control as needed. Or simply ignore this implementation.

 ``` objective-c
 -(void) PJPollWillDismiss:(PJPoll*) poll;
 ```
 
The polljoy poll UI is finished and will dismiss. You can do whatever UI control as needed. Or simply ignore this implementation. You can prepare your own UI before resuming your app before the polljoy poll UI is dismissed.

 ``` objective-c
 -(void) PJPollDidDismiss:(PJPoll*) poll;
 ```
 
The polljoy poll UI is finished and has dismissed. You can do whatever UI control as needed. Or simply ignore this implementation. You can prepare your own UI to resume your app before the polljoy UI is dismissed. This is the last callback from polljoy and all polls are completed. You should resume your app if you have paused.

 ``` objective-c
 -(void) PJPollDidResponded:(PJPoll*) poll;
 ```
 
User has responded to the poll. The poll will contain all the poll data including user?™s responses. You can ignore this (the results are displayed in the polljoy.com admin console and able to be exported) or use it as you wish.
If you issue a virtual currency amount to user, you MUST implement this method to handle the virtual amount issued (especially if your app is game). This is the only callback from SDK that informs the app the virtual amount that the user collected.

 ``` objective-c
 -(void) PJPollDidSkipped:(PJPoll*) poll;
 ```
 
 If the poll is not mandatory, user can choose to skip the poll. You can handle this case or simply ignore it safely.
 
-
#### Got questions? Email us at help@polljoy.com

## Version History

### Version 1.4.2
 - bug fix

### Version 1.4.1
 - bug fix in pollId value

### Version 1.4
 - update API spec

### Version 1.3
 - change API end point to SSL, fix display problem in long question and long answer

### Version 1.2
 - add session monitor when requesting poll

### Version 1.1, 2014/04/15
 - fix a bug in background thread when there is only one poll without default image setup

### Version 1.0, 2014/04/14
 - initial release
