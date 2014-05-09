package com.polljoy.util;

import android.graphics.Color;
import android.util.Log;

public class PJColorHelper {
	private static final String TAG = "PJColorHelper";

	public static int colorForString(String string) {
		try {
			String colorString = "#" + string;
			int color = Color.parseColor(colorString);
			return color;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Log.w(TAG, "Color parsing error from string: " + string);
			e.printStackTrace();
		}
		return 0;
	}
}
