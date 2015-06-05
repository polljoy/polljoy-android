package com.polljoy.internal;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.io.FileOutputStream;
import java.security.MessageDigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.graphics.Bitmap;

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
	
	public static String createFilenameFromUrl(Context context, String url, String ext)
	{
		String cacheDirectory = context.getCacheDir() + File.separator + "polljoy";
		File dir = new File(cacheDirectory);
        String filename = null;
        try {
            filename = sha1(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dir.exists()) {
			if (!dir.mkdir()){
				Log.e("polljoy", "cannot create cache folder: " + cacheDirectory);
			}
		}

        return cacheDirectory + File.separator + filename + "." + ext;
	}

    public static String saveImageCache(Bitmap bitmapImage, String filename){
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(filename);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static String sha1(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(s.getBytes());
        byte[] bytes = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            buffer.append(tmp);
        }
        return buffer.toString();
    }
}
