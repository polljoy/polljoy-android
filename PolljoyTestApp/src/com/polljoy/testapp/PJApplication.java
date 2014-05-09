package com.polljoy.testapp;

import com.polljoy.Polljoy;

import android.app.Application;

public class PJApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Polljoy.setSandboxMode(true);

		// register your Polljoy session here.
		// check YOUR_APP_ID in Polljoy admin panel
		// ** please remember to add an app and assign to the polls you created.
		Polljoy.startSession(this, "YOUR_APP_ID");

	}

}
