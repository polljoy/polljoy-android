package com.polljoy.testapp;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.polljoy.PJPoll;
import com.polljoy.PJResponseStatus;
import com.polljoy.PJUserType;
import com.polljoy.Polljoy;
import com.polljoy.Polljoy.PolljoyDelegate;

public class MainActivity extends Activity implements PolljoyDelegate {
	EditText appIdEditText;
	EditText userIdEditText;
	EditText appVersionEditText;
	EditText levelEditText;
	EditText sessionCountEditText;
	EditText timeSinceInstallEditText;
	RadioGroup userTypeRadioGroup;
	TextView sessionIdTextView;
	Button showPollButton;
	TextView logTextView;
	Timer updateSessionTimer;
	ScrollView logScrollView;
	String log = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intializeUIControls();
		// schedule a timer to check for session state if you want to monitor it
		scheduleUpdateLogTimer();

		// set this if you want poll to show automatically when ready
		Polljoy.setAutoShow(true);

		// you should request for poll at somehwhere you need
		requestPoll(null);
	}


	private void intializeUIControls() {
		setContentView(R.layout.activity_main);
		appIdEditText = (EditText) this.findViewById(R.id.appIdEditText);
		userIdEditText = (EditText) this.findViewById(R.id.userIdEditText);
		appVersionEditText = (EditText) this
				.findViewById(R.id.appVersionEditText);
		levelEditText = (EditText) this.findViewById(R.id.levelEditText);
		sessionCountEditText = (EditText) this
				.findViewById(R.id.sessionCountEditText);
		timeSinceInstallEditText = (EditText) this
				.findViewById(R.id.timeSinceInstallEditText);
		userTypeRadioGroup = (RadioGroup) this
				.findViewById(R.id.userTypeRadioGroup);
		userTypeRadioGroup.check(R.id.nonPayRadioButton);
		sessionIdTextView = (TextView) this
				.findViewById(R.id.sessionIdTextView);
		showPollButton = (Button) findViewById(R.id.showPollButton);
		logTextView = (TextView) this.findViewById(R.id.logTextView);
		logScrollView = (ScrollView) this.findViewById(R.id.logScrollView);
	}

	private void scheduleUpdateLogTimer() {
		if (updateSessionTimer != null) {
			return;
		}
		updateSessionTimer = new Timer();
		updateSessionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateSessionLog();
					}
				});
			}

		}, 15 * 1000, 15 * 1000);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	static boolean hasShownAppInfo = false;

	public void updateSessionLog() {
		try {
			sessionIdTextView.setText(Polljoy.getSessionId());
			userIdEditText.setText(Polljoy.getUserId());
			appIdEditText.setText(Polljoy.getAppId());
			sessionCountEditText.setText(String.valueOf(Polljoy.getSession()));
			timeSinceInstallEditText.setText(String.valueOf(Polljoy
					.getTimeSinceInstall()));
			if (Polljoy.getSessionId() != null) {
				if (!hasShownAppInfo) {
					logTrace("Device Id: " + Polljoy.getDeviceId());
					logTrace("App Name: " + Polljoy.getApp().getAppName());
					logTrace("defaultImageUrl: "
							+ Polljoy.getApp().getDefaultImageUrl());
					logTrace("maximumPollPerSession: "
							+ Polljoy.getApp().getMaximumPollPerSession());
					logTrace("maximumPollPerDay: "
							+ Polljoy.getApp().getMaximumPollPerDay());
					logTrace("maximumPollInARow: "
							+ Polljoy.getApp().getMaximumPollInARow());
					logTrace("backgroundColor: "
							+ Integer.toHexString(Polljoy.getApp()
									.getBackgroundColor()));
					logTrace("borderColor: "
							+ Integer.toHexString(Polljoy.getApp()
									.getBorderColor()));
					logTrace("buttonColor: "
							+ Integer.toHexString(Polljoy.getApp()
									.getButtonColor()));
					logTrace("fontColor: "
							+ Integer.toHexString(Polljoy.getApp()
									.getFontColor()));
					hasShownAppInfo = true;
				}
			}
			logTextView.setText(log);
			scrollLogToBottom();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void renewSession(View view) {
		String appId = appIdEditText.getText().toString();
		Polljoy.startSession(this.getApplication(), appId);
		this.requestPoll(null);
	}

	public void requestPoll(View view) {
		appIdEditText.setText(Polljoy.getAppId());
		String appVersion = this.getVersionFromString(appVersionEditText
				.getText().toString());
		int level = Integer.getInteger(levelEditText.getText().toString(), 0);
		int sessionCount = Integer.getInteger(
				sessionCountEditText.getText().toString(), 0).intValue();
		int timeSinceInstall = Integer.getInteger(
				timeSinceInstallEditText.getText().toString(), 0).intValue();
		int checkedId = userTypeRadioGroup.getCheckedRadioButtonId();
		PJUserType userType = PJUserType.PJNonPayUser;
		if (checkedId == R.id.payRadioButton) {
			userType = PJUserType.PJPayUser;
		}
		// Polljoy.getPoll();// no delegate call, will autoshow by default
		Polljoy.getPoll(appVersion, level, sessionCount, timeSinceInstall,
				userType, this);
	}

	private String getVersionFromString(String originalString) {
		String defaultVersionString = "0";
		String result = defaultVersionString;
		try {
			String appVersion = originalString;
			appVersion = appVersion.replace(" ", "");
			if (appVersion.length() < 1) {
				appVersion = defaultVersionString;
			}
			result = appVersion;
		} catch (NullPointerException e) {
			return defaultVersionString;
		}
		return result;
	}

	public void showPoll(View view) {
		Polljoy.showPoll();
	}

	@Override
	public void PJPollNotAvailable(PJResponseStatus status) {
		logTrace("PJPollNotAvailable status: " + status);

	}

	@Override
	public void PJPollIsReady(ArrayList<PJPoll> polls) {
		// pause your app and save any status if needed

		logTrace("PJPollIsReady: " + polls.toString());

		// update session log
		updateSessionLog();

		showPollButton.setEnabled(true);

		// trigger to show the poll when you are ready
		Polljoy.showPoll();

	}

	@Override
	public void PJPollWillShow(PJPoll poll) {
		logTrace("PJPollWillShow " + poll.toString());

	}

	@Override
	public void PJPollDidShow(PJPoll poll) {
		logTrace("PJPollDidShow " + poll.toString());

	}

	@Override
	public void PJPollWillDismiss(PJPoll poll) {
		logTrace("PJPollWillDismiss " + poll.toString());
	}

	@Override
	public void PJPollDidDismiss(PJPoll poll) {
		logTrace("PJPollDidDismiss " + poll.toString());
		if (Polljoy.getPolls().size() > 0) {
			showPollButton.setEnabled(true);
		} else {
			showPollButton.setEnabled(false);
		}
	}

	@Override
	public void PJPollDidResponded(PJPoll poll) {
		// user response to the poll
		// check if any vrtual money is received and update your app status
		//
		// poll.virtualAmount
		logTrace("PJPollDidResponded " + poll.toString());

		if (poll.getVirtualAmount() > 0) {
			logTrace("Virtual Amount: " + poll.getVirtualAmount());
		}

		if (Polljoy.getPolls().size() > 0) {
			showPollButton.setEnabled(true);
		} else {
			showPollButton.setEnabled(false);
		}
	}

	@Override
	public void PJPollDidSkipped(PJPoll poll) {
		// user skipped to respose to the poll
		// no virtual money will be allocated
		logTrace("PJPollDidSkipped " + poll.toString());
		if (Polljoy.getPolls().size() > 0) {
			showPollButton.setEnabled(true);
		} else {
			showPollButton.setEnabled(false);
		}
	}

	void scrollLogToBottom() {
		logScrollView.fullScroll(ScrollView.FOCUS_DOWN);

	}

	void logTrace(String text) {
		log = log + text + "\n";

	}
}
