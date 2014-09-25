package com.polljoy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

public class PJScreenConfiguration {

	Point screenSize;
	PJScreenType screenType;
	double baseScale;
	double shortSideDesignScale;
	double longSideDesignScale;
	int fontSize;
	int rewardFontSize;
	double innerWidth;
	double innerHeight;
	static private Method getWidthMethod = null;
	static private Method getHeightMethod = null;

	PJScreenConfiguration(Activity activity, int orientation) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		this.screenSize = PJScreenConfiguration.getRealSizeForDisplay(display);
		double borderLongSideDesignLength = 1;
		double borderShortSideDesignLength = 1;
		double canvasLongSideDesignLength = 1;
		double canvasShortSideDesignLength = 1;
		this.screenType = PJScreenType.screenTypeForScreenSize(screenSize);
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			switch (this.screenType) {
			case PJScreenType_16x9:
				canvasLongSideDesignLength = 2048;
				canvasShortSideDesignLength = 1200;
				borderLongSideDesignLength = 1600;
				borderShortSideDesignLength = 900;
				break;
			case PJScreenType_3x2:
				canvasLongSideDesignLength = 1920;
				canvasShortSideDesignLength = 1200;
				borderLongSideDesignLength = 1400;
				borderShortSideDesignLength = 900;
				break;
			case PJScreenType_4x3:
				canvasLongSideDesignLength = 2048;
				canvasShortSideDesignLength = 1536;
				borderLongSideDesignLength = 1500;
				borderShortSideDesignLength = 1120;
				break;
			default:
				break;
			}
		} else {
			switch (this.screenType) {
			case PJScreenType_16x9:
				canvasLongSideDesignLength = 2048;
				canvasShortSideDesignLength = 1200;
				borderLongSideDesignLength = 1600;
				borderShortSideDesignLength = 900;
				break;
			case PJScreenType_3x2:
				canvasLongSideDesignLength = 1920;
				canvasShortSideDesignLength = 1200;
				borderLongSideDesignLength = 1560;
				borderShortSideDesignLength = 750;
				break;
			case PJScreenType_4x3:
				canvasLongSideDesignLength = 2048;
				canvasShortSideDesignLength = 1536;
				borderLongSideDesignLength = 1500;
				borderShortSideDesignLength = 1120;
				break;
			default:
				break;
			}
		}
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		double displayWidth = metrics.widthPixels;
		double displayHeight = metrics.heightPixels;
		double statusBarHeight = this.getStatusBarHeight(activity);
		displayHeight -= statusBarHeight;
		double longSideLength = Math.max(displayWidth, displayHeight);
		double shortSideLength = Math.min(displayWidth, displayHeight);

		longSideDesignScale = borderLongSideDesignLength
				/ canvasLongSideDesignLength;
		shortSideDesignScale = borderShortSideDesignLength
				/ canvasShortSideDesignLength;
		double canvasRealLongSide = 1;
		double canvasRealShortSide = 1;
		this.baseScale = Math.min(longSideLength / canvasLongSideDesignLength,
				shortSideLength / canvasShortSideDesignLength);
		canvasRealLongSide = canvasLongSideDesignLength * this.baseScale;
		canvasRealShortSide = canvasShortSideDesignLength * this.baseScale;
		double borderRealLongSide = canvasRealLongSide * longSideDesignScale;
		double borderRealShortSide = canvasRealShortSide * shortSideDesignScale;
		float pixelsPerDip = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
		double compensation = 1.5 * pixelsPerDip;
		borderRealLongSide += compensation;
		borderRealShortSide += compensation;
		switch (orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			this.innerWidth = borderRealLongSide;
			this.innerHeight = borderRealShortSide;
			this.fontSize = (int) (borderRealLongSide * 3.516 * 0.01);
			this.rewardFontSize = (int) (borderRealShortSide * 12.5 / 2.0 * 0.01 * 0.9);
			break;
		case Configuration.ORIENTATION_PORTRAIT:
		default:
			this.innerHeight = borderRealLongSide;
			this.innerWidth = borderRealShortSide;
			this.fontSize = (int) (borderRealLongSide * 3.516 * 0.01);
			this.rewardFontSize = (int) (borderRealLongSide * 2.812 * 0.01);
		}
	}

	public int getStatusBarHeight(Activity activity) {
		int result = 0;
		int resourceId = activity.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = activity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	double dimensionWithPercentage(double base, double percentage) {
		return base * percentage / 100.0;
	}

	int widthWithPercentage(double percentage) {
		return (int) this.dimensionWithPercentage(this.innerWidth, percentage);
	}

	int heightWithPercentage(double percentage) {
		return (int) this.dimensionWithPercentage(this.innerHeight, percentage);
	}

    double getInnerWidth () {
        return this.innerWidth;
    }

    double getInnerHeight () {
        return this.innerHeight;
    }

	static Point getRealSizeForDisplay(Display display) {
		int sdkCode = Build.VERSION.SDK_INT;
		if (sdkCode < Build.VERSION_CODES.HONEYCOMB_MR2) {
			return getRealSizeForDevicesForHoneycombMR1AndBelow(display);
		} else if (sdkCode < Build.VERSION_CODES.JELLY_BEAN) {
			return getRealSizeForDevicesBetweenHoneycombMR2AndJellyBean(display);
		} else {
			return getRealSizeForDevicesForJellyBeanMR1AndAbove(display);
		}
	}

	@SuppressWarnings("deprecation")
	static Point getRealSizeForDevicesForHoneycombMR1AndBelow(Display display) {
		int width = display.getWidth();
		int height = display.getHeight();
		Point size = new Point(width, height);
		return size;
	}

	static Point getRealSizeForDevicesBetweenHoneycombMR2AndJellyBean(
			Display display) {
		try {
			if (getWidthMethod == null) {
				getWidthMethod = display.getClass().getMethod("getRawWidth");

			}
			if (getHeightMethod == null) {
				getHeightMethod = display.getClass().getMethod("getRawHeight");
			}
			int width = (Integer) getWidthMethod.invoke(display);
			int height = (Integer) getHeightMethod.invoke(display);
			return new Point(width, height);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return new Point(1, 1);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	static Point getRealSizeForDevicesForJellyBeanMR1AndAbove(Display display) {
		Point size = new Point();
		display.getRealSize(size);
		return size;
	}

}
