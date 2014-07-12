package com.polljoy;

import android.graphics.Point;

public enum PJScreenType {
	PJScreenTypeUnknown, PJScreenType_16x9, PJScreenType_3x2, PJScreenType_4x3;
	static PJScreenType screenTypeForScreenSize(Point screenSize) {
		int width = screenSize.x;
		int height = screenSize.y;
		int longSide = Math.max(width, height);
		int shortSide = Math.min(width, height);
		if (width <= 0) {
			return PJScreenTypeUnknown;
		}
		float aspectRatio = (float) longSide / (float) shortSide;
		if (aspectRatio >= 16.0 / 9.0) {
			// 16:9
			return PJScreenType_16x9;

		} else if (aspectRatio >= 16.0 / 10.0) {
			// 16:10
			return PJScreenType_3x2;

		} else if (aspectRatio >= 3.0 / 2.0) {
			// 3:2
			return PJScreenType_3x2;

		} else if (aspectRatio >= 4.0 / 3.0) {
			// 4:3
			return PJScreenType_4x3;
		}
		return PJScreenTypeUnknown;
	}
}
