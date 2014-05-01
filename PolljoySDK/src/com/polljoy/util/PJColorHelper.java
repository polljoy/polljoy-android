package com.polljoy.util;

import android.graphics.Color;

public class PJColorHelper {
	public static int colorForString(String string) {
		try {
			String colorString = "#" + string;
			int color = Color.parseColor(colorString);
			return color;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
