package com.polljoy.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;

import com.polljoy.R;

public class PolljoyCore {
	public static String getDeviceId(Context context) {
		String key = "identifier";
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String identifier = sharedPrefs.getString(key, null);
		if(identifier == null) {
			identifier = generateRandomId();
			sharedPrefs.edit().putString(key, identifier).commit();
		}
		return identifier;
	}

	public static String getDeviceModel() {
		String model = Build.MODEL;
		return model;
	}

	public static boolean isAmazonKindle() {
	String manufacturer = Build.MANUFACTURER;
	try {
		if(manufacturer.equalsIgnoreCase("amazon")) {
			String model = Build.MODEL.toLowerCase(Locale.US);
//			String[] kindleFireModels = {"KFAPWA","KFAPWI","KFTHWA","KFTHWI","KFSOWI","KFJWA","KFJWI","KFTT","KFOT","Kindle Fire"};
			if(model.startsWith("kf") || model.startsWith("kindle")) {
				return true;
			}
		}
	} catch(NullPointerException e) {
	}

	return false;
}
	public static Drawable defaultImage(Context context) {
		if (context != null) {
			return context.getResources().getDrawable(R.drawable.polljoy);
		}
		return null;
	}

	public static String generateRandomId() {
		return UUID.randomUUID() + "R";
	}

	public static String getVendorID() {
		return UUID.fromString(Build.MANUFACTURER).toString();
	}

	public synchronized static long getTimeSinceInstall(Context context) {
		Date now = Calendar.getInstance().getTime();
		long endDay = now.getTime() / 1000 / 60 / 60 / 24;
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		long startDay = sharedPrefs.getLong("dayInstalled", 0);
		if (startDay > 0) {
		} else {
			startDay = endDay;
			sharedPrefs.edit().putLong("dayInstalled", startDay).commit();
		}

		long daysBetween = endDay - startDay;
		return daysBetween;
	}

	public synchronized static int getNewSession(Context context) {
		String key = "session";
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int session = sharedPrefs.getInt(key, 0) + 1;
		sharedPrefs.edit().putInt(key, session).commit();
		return session;
	}

	public synchronized static int getCurrentSession(Context context) {
		String key = "session";
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int session = sharedPrefs.getInt(key, 0);
		if (session < 1) {
			return getNewSession(context);
		} else {
			return session;
		}
	}
}
