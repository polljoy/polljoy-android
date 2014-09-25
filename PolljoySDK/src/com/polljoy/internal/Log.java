package com.polljoy.internal;

public class Log {
	public static final boolean loggingEnabled = true;

	public static void v(String tag, String msg) {
		if (loggingEnabled) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (loggingEnabled) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (loggingEnabled) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (loggingEnabled) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (loggingEnabled) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable e) {

		if (loggingEnabled) {
			android.util.Log.w(tag, msg, e);
		}
	}
}
