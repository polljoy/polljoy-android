![Picture](http://www.polljoy.com/assets/images/logo/polljoy-logo-github.png)

In-app polls made easy. Just 2 API calls.


#Intro
Hi friend! Let's add polljoy to your amazing Android app. It's simple, you'll be up in minutes.

Questions? - email help@polljoy.com and one of our devs will assist!

#Web console
Polls are created and managed through our web interface - https://admin.polljoy.com

#Steps
1.	Copy the Polljoy SDK Archived File to your project workspace folder and unzip. You'll see two projects: PolljoySDK and PolljoyTestApp.
2.	In Eclipse, import the PolljoySDK project into your workspace.
3.	Add the PolljoySDK project as a reference project in your project settings:

  ![Picture](Doc/setup.png)

4. Add INTERNET permission to your project’s AndroidManifest.xml:

 ``` java
 <uses-permission android:name="android.permission.INTERNET" />
 ```
 
5. Again in `AndroidManifest.xml`, declare `PJPollViewActivity` by adding the following lines between `<application>` and `</application>`:

 ``` java
 <activity
 android:name="com.polljoy.PJPollViewActivity"
 android:configChanges="orientation|screenSize|keyboardHidden"
 android:launchMode="singleTop"
 android:theme="@android:style/Theme.Translucent" >
 </activity>
 ```


###Start session
polljoy works in the background to avoid interrupting your app’s main thread.

Each app starts a session and gets the **Session ID** for all communications to the API. To have best performance and integration, we recommend registering the session at startup. You’ll need your **App ID** (grab it in the web [admin panel](https://admin.polljoy.com/applications/app))

First import the Polljoy package by adding the following code when you call methods within PolljoySDK:
 
 ``` java
 import com.polljoy.Polljoy;
 ``` 

On app startup, call Polljoy to register a session with `ApplicationContext` and your **App ID**. You can either add the lines in your custom Application class or your LAUNCHER activity.
 
 i.	If you add it In your launcher activity: 

 ``` java
 // ...
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Polljoy.startSession(this.getApplicationContext(), " YOUR_APP_ID");
 // ...
 ```

 ii.	Or if you add it in your custom Application class,
 
 ``` java
 // ...
	public void onCreate() {
		super.onCreate();
		Polljoy.startSession(this, "YOUR_APP_ID");
 // ...
 ```
 
The SDK will automatically handle all session control and required information to get the correct poll based on your poll setup in admin panel and save your poll result for analysis. These includes the session ID, session count, time (days) since first call polljoy SDK, device ID, platform, OS version … etc.

Each time you call `startSession`, the SDK will increase the session count by 1. So, you should only call it once for each launch to get the session count correct.

Once the session is started, SDK will cache all app settings including the default image, border image and button image (if any) that you have setup in the [admin panel](https://admin.polljoy.com). After caching, there will be no operation until you request polls from polljoy service.
 
### Get poll (simple)
After you start the session, you can get polls any time and place you want!

In your program logic, import `com.polljoy.Polljoy; ` where you want to get polls. Then call:
 ``` java
  // ...
     Polljoy.getPoll();
  // use this if you don't need to handle callbacks from polljoy
  // this will auto show the polls when all polls are ready
  // ...
 ```
 
`Note: these are simple version if you will only select polls based on session, timeSinceInstall and platform, or not have any seletion criteria.  If you want more than these, use the full version that follows.

###Get poll (full)
 ``` java
 // ...
   Polljoy.getPoll(appVersion, 
   		   level,
   		   session,
   		   timeSinceInstall,
   		   userType,
   		   tags,
   		   delegate);
 // ...
 ```
  
In summary:

`appVersion` (optional) Set to null if you prefer not to send it.  Or you can choose to pass it. eg 1.0.35

`level` (optional) Set as 0 if you prefer not to send it. If your app has levels you can pass them here. eg 34 

`session` (optional) Set it as 0 and the SDK will send it for you.  Or you can manually send it. eg 3 

`timeSinceInstall` (optional) Set it as 0 and the SDK will send it for you.  Or you can manually set it by sending a value for how long the app has been installed (by default, counted in days). eg 5

`userType` Pass back either **Pay** or **Non-Pay**. This is the `ENUM PJUserType` as defined in `Polljoy.java`

`tags` (optional) Set to null if you aren't using them.  If your game uses tags to select polls, pass them in string format with as many as you want to send - `TAG,TAG, ... ,TAG`.  TAG is either in the format TAGNAME or TAGNAME:VALUE.  They should match what you defined in the web console. An example of sending back player gender, current energy and where the poll is being called from could be: `MALE,ENERGY#18,PVPMENU`

`delegate` (optional) Set to null if not needed. Delegate is the instance to handle all callbacks from polljoy SDK. If used, the delegate should implement `PolljoyDelegate` as defined in `Polljoy.java`

Please check `Polljoy.java` for the type of the parameters. polljoy's API is open. All data returned is passed back to the delegate. Delegate can use the returned poll data for their own control if needed.

### Callbacks

polljoy will inform delegate at different stages when the polls are downloaded, ready to show, user responded etc. The game can optionally implement the delegate methods to control the app logic. The delegate methods are:

 ``` java
 void PJPollNotAvailable(PJResponseStatus status);
 ```
 
When there is no poll matching your selection criteria or no more polls to show in the current session.

 ``` java
 void PJPollIsReady(ArrayList<PJPoll> polls);
 ```
 
When poll/s is/are ready to show (including all associated images). Friendly tip - If you are displaying the poll in the middle of an active game or app session that needs real time control, consider to pause your game before presenting the poll UI as needed. 

The polls array returned are all the matched polls for the request. Please refer `PJPoll.h` for the data structure.
When you’re ready to present the poll, call:

 ``` java
 Polljoy.showPoll();
 ```

This will present the polljoy UI according to your app style and poll settings. Then the SDK will handle all the remaining tasks for you. These include handling the user’s response, informing delegate for any virtual amount user received, uploading the result to the console … etc.

We recommend you implement this delegate method so you know when polls are ready and call polljoy SDK to show the poll or do whatever control you need.

 ``` java
 void PJPollWillShow(PJPoll poll);
 ```
 
The polljoy poll UI is ready and will show. You can do whatever UI control as needed. Or simply ignore this implementation.

 ``` java
 void PJPollDidShow:(PJPoll poll);
 ```
 
The polljoy poll UI is ready and has shown. You can do whatever UI control as needed. Or simply ignore this implementation.

 ``` java
 void PJPollWillDismiss:(PJPoll poll);
 ```
 
The polljoy poll UI is finished and will dismiss. You can do whatever UI control as needed. Or simply ignore this implementation. You can prepare your own UI before resuming your game before the polljoy poll UI is dismissed.

 ``` java
 void PJPollDidDismiss(PJPoll poll);
 ```
 
The polljoy poll UI is finished and has dismissed. You can do whatever UI control as needed. Or simply ignore this implementation. You can prepare your own UI to resume your game before the polljoy UI is dismissed. This is the last callback from polljoy and all polls are completed. You should resume your game if you have paused.

 ``` java
 void PJPollDidResponded(PJPoll poll);
 ```
 
User has responded to the poll. The poll will contain all the poll data including user’s responses. You can ignore this (the results are displayed in the web admin console and able to be exported) or use it as you wish.
If you issue a virtual currency amount to user, you MUST implement this method to handle the virtual amount issued. This is the only callback from SDK that informs the game the virtual amount that the user collected.

 ``` java
 void PJPollDidSkipped(PJPoll poll);
 ```
 
 If the poll is not mandatory, the user can choose to skip the poll. You can handle this case or simply ignore it safely.

-
That's it!  Email us at help@polljoy.com if you have questions or suggestions!

ps - love robots? [how about penguins?](https://polljoy.com/world.html)
