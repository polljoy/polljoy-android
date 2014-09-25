package com.polljoy;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;

import com.polljoy.util.PJColorHelper;
import com.polljoy.util.PJDateHelper;

public class PJApp implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3745183264260571339L;
	String appId = null;
	String appName = null;
	String defaultImageUrl = null;
	int maximumPollPerSession = 0;
	int maximumPollPerDay = 0;
	int maximumPollInARow = 0;
	int backgroundColor = 0x000000;
	int borderColor = 0x000000;
	int buttonColor = 0x000000;
	int fontColor = 0x000000;

	int backgroundAlpha = 100;
	int backgroundCornerRadius = 0;
	String backgroundImageUrl = null;
	PJPollImageUrlSet imageUrlSet_16x9;
	PJPollImageUrlSet imageUrlSet_3x2;
	PJPollImageUrlSet imageUrlSet_4x3;

	int borderWidth = 0;
	int buttonFontColor = 0x000000;
	Boolean buttonShadow = false;
	String closeButtonImageUrl = null;
	PJCloseButtonLocation closeButtonLocation = PJCloseButtonLocation.TopRight;
	int closeButtonOffsetX = 0;
	int closeButtonOffsetY = 0;
    boolean closeButtonEasyClose = false;
	String fontName = null;
	int imageCornerRadius = 0;
	Date modifiedDate = null;
	int overlayAlpha = 100;
	String rewardImageUrl = null;

	PJApp(JSONObject jsonObject) {
		this.appId = jsonObject.optString("appId");
		this.appName = jsonObject.optString("appName");
		this.defaultImageUrl = jsonObject.optString("defaultImageUrl");
		this.maximumPollPerSession = jsonObject.optInt("maxPollsPerSession");
		this.maximumPollPerDay = jsonObject.optInt("maxPollsPerDay");
		this.maximumPollInARow = jsonObject.optInt("maxPollsInARow");
		this.backgroundColor = PJColorHelper.colorForString(jsonObject
				.optString("backgroundColor"));
		this.borderColor = PJColorHelper.colorForString(jsonObject
				.optString("borderColor"));
		this.buttonColor = PJColorHelper.colorForString(jsonObject
				.optString("buttonColor"));
		this.fontColor = PJColorHelper.colorForString(jsonObject
				.optString("fontColor"));
		this.backgroundAlpha = jsonObject.optInt("backgroundAlpha");
		this.backgroundCornerRadius = jsonObject
				.optInt("backgroundCornerRadius");
		this.backgroundImageUrl = jsonObject.optString("backgroundImageUrl");

		this.rewardImageUrl = jsonObject.optString("rewardImageUrl");
		this.closeButtonImageUrl = jsonObject.optString("closeButtonImageUrl");
		this.imageCornerRadius = jsonObject.optInt("imageCornerRadius");

		String borderImageUrl_16x9_L = jsonObject
				.optString("borderImageUrl_16x9_L");
		String borderImageUrl_16x9_P = jsonObject
				.optString("borderImageUrl_16x9_P");
		String borderImageUrl_3x2_L = jsonObject
				.optString("borderImageUrl_3x2_L");
		String borderImageUrl_3x2_P = jsonObject
				.optString("borderImageUrl_3x2_P");
		String borderImageUrl_4x3_L = jsonObject
				.optString("borderImageUrl_4x3_L");
		String borderImageUrl_4x3_P = jsonObject
				.optString("borderImageUrl_4x3_P");
		String buttonImageUrl_16x9_L = jsonObject
				.optString("buttonImageUrl_16x9_L");
		String buttonImageUrl_16x9_P = jsonObject
				.optString("buttonImageUrl_16x9_P");
		String buttonImageUrl_3x2_L = jsonObject
				.optString("buttonImageUrl_3x2_L");
		String buttonImageUrl_3x2_P = jsonObject
				.optString("buttonImageUrl_3x2_P");
		String buttonImageUrl_4x3_L = jsonObject
				.optString("buttonImageUrl_4x3_L");
		String buttonImageUrl_4x3_P = jsonObject
				.optString("buttonImageUrl_4x3_P");
		this.imageUrlSet_16x9 = new PJPollImageUrlSet(this.rewardImageUrl,
				this.closeButtonImageUrl, this.defaultImageUrl,
				borderImageUrl_16x9_L, borderImageUrl_16x9_P,
				buttonImageUrl_16x9_L, buttonImageUrl_16x9_P,
				this.imageCornerRadius);
		this.imageUrlSet_3x2 = new PJPollImageUrlSet(this.rewardImageUrl,
				this.closeButtonImageUrl, this.defaultImageUrl,
				borderImageUrl_3x2_L, borderImageUrl_3x2_P,
				buttonImageUrl_3x2_L, buttonImageUrl_3x2_P,
				this.imageCornerRadius);
		this.imageUrlSet_4x3 = new PJPollImageUrlSet(this.rewardImageUrl,
				this.closeButtonImageUrl, this.defaultImageUrl,
				borderImageUrl_4x3_L, borderImageUrl_4x3_P,
				buttonImageUrl_4x3_L, buttonImageUrl_4x3_P,
				this.imageCornerRadius);

		this.borderWidth = jsonObject.optInt("borderWidth");
		this.buttonFontColor = PJColorHelper.colorForString(jsonObject
				.optString("buttonFontColor"));

		this.buttonShadow = jsonObject.optBoolean("buttonShadow");
		int locationCode = jsonObject.optInt("closeButtonLocation");
		this.closeButtonLocation = PJCloseButtonLocation
				.locationForCode(locationCode);
		this.closeButtonOffsetX = jsonObject.optInt("closeButtonOffsetX");
		this.closeButtonOffsetY = jsonObject.optInt("closeButtonOffsetY");
        this.closeButtonEasyClose = jsonObject.optBoolean("closeButtonEasyClose");
		this.fontName = jsonObject.optString("fontName");
		String dateString = jsonObject.optString("modified");
		this.modifiedDate = PJDateHelper.parseDateString(dateString);
		this.overlayAlpha = jsonObject.optInt("overlayAlpha");
	}

	public PJPollImageUrlSet imageUrlSetForScreenType(PJScreenType screenType) {
		switch (screenType) {

		case PJScreenType_16x9:
			return this.imageUrlSet_16x9;
		case PJScreenType_3x2:
			return this.imageUrlSet_3x2;
		case PJScreenType_4x3:
			return this.imageUrlSet_4x3;
		case PJScreenTypeUnknown:
		default:
			return this.imageUrlSet_4x3;
		}
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDefaultImageUrl() {
		return defaultImageUrl;
	}

	public void setDefaultImageUrl(String defaultImageUrl) {
		this.defaultImageUrl = defaultImageUrl;
	}

	public int getMaximumPollPerSession() {
		return maximumPollPerSession;
	}

	public void setMaximumPollPerSession(int maximumPollPerSession) {
		this.maximumPollPerSession = maximumPollPerSession;
	}

	public int getMaximumPollPerDay() {
		return maximumPollPerDay;
	}

	public void setMaximumPollPerDay(int maximumPollPerDay) {
		this.maximumPollPerDay = maximumPollPerDay;
	}

	public int getMaximumPollInARow() {
		return maximumPollInARow;
	}

	public void setMaximumPollInARow(int maximumPollInARow) {
		this.maximumPollInARow = maximumPollInARow;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public int getButtonColor() {
		return buttonColor;
	}

	public void setButtonColor(int buttonColor) {
		this.buttonColor = buttonColor;
	}

	public int getFontColor() {
		return fontColor;
	}

	public void setFontColor(int fontColor) {
		this.fontColor = fontColor;
	}

	public int getBackgroundAlpha() {
		return backgroundAlpha;
	}

	public void setBackgroundAlpha(int backgroundAlpha) {
		this.backgroundAlpha = backgroundAlpha;
	}

	public int getBackgroundCornerRadius() {
		return backgroundCornerRadius;
	}

	public void setBackgroundCornerRadius(int backgroundCornerRadius) {
		this.backgroundCornerRadius = backgroundCornerRadius;
	}

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	public void setBackgroundImageUrl(String backgroundImageUrl) {
		this.backgroundImageUrl = backgroundImageUrl;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getButtonFontColor() {
		return buttonFontColor;
	}

	public void setButtonFontColor(int buttonFontColor) {
		this.buttonFontColor = buttonFontColor;
	}

	public Boolean getButtonShadow() {
		return buttonShadow;
	}

	public void setButtonShadow(Boolean buttonShadow) {
		this.buttonShadow = buttonShadow;
	}

	public String getCloseButtonImageUrl() {
		return closeButtonImageUrl;
	}

	public void setCloseButtonImageUrl(String closeButtonImageUrl) {
		this.closeButtonImageUrl = closeButtonImageUrl;
	}

	public PJCloseButtonLocation getCloseButtonLocation() {
		return closeButtonLocation;
	}

	public void setCloseButtonLocation(PJCloseButtonLocation closeButtonLocation) {
		this.closeButtonLocation = closeButtonLocation;
	}

	public int getCloseButtonOffsetX() {
		return closeButtonOffsetX;
	}

	public void setCloseButtonOffsetX(int closeButtonOffsetX) {
		this.closeButtonOffsetX = closeButtonOffsetX;
	}

	public int getCloseButtonOffsetY() {
		return closeButtonOffsetY;
	}

	public void setCloseButtonOffsetY(int closeButtonOffsetY) {
		this.closeButtonOffsetY = closeButtonOffsetY;
	}

    public boolean getCloseButtonEasyClose () {return closeButtonEasyClose;}

    public void setCloseButtonEasyClose(boolean closeButtonEasyClose) {
        this.closeButtonEasyClose = closeButtonEasyClose;
    }

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public int getImageCornerRadius() {
		return imageCornerRadius;
	}

	public void setImageCornerRadius(int imageCornerRadius) {
		this.imageCornerRadius = imageCornerRadius;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getOverlayAlpha() {
		return overlayAlpha;
	}

	public void setOverlayAlpha(int overlayAlpha) {
		this.overlayAlpha = overlayAlpha;
	}

	public String getRewardImageUrl() {
		return rewardImageUrl;
	}

	public void setRewardImageUrl(String rewardImageUrl) {
		this.rewardImageUrl = rewardImageUrl;
	}

	public PJPollImageUrlSet getImageUrlSet_16x9() {
		return imageUrlSet_16x9;
	}

	public void setImageUrlSet_16x9(PJPollImageUrlSet imageUrlSet_16x9) {
		this.imageUrlSet_16x9 = imageUrlSet_16x9;
	}

	public PJPollImageUrlSet getImageUrlSet_3x2() {
		return imageUrlSet_3x2;
	}

	public void setImageUrlSet_3x2(PJPollImageUrlSet imageUrlSet_3x2) {
		this.imageUrlSet_3x2 = imageUrlSet_3x2;
	}

	public PJPollImageUrlSet getImageUrlSet_4x3() {
		return imageUrlSet_4x3;
	}

	public void setImageUrlSet_4x3(PJPollImageUrlSet imageUrlSet_4x3) {
		this.imageUrlSet_4x3 = imageUrlSet_4x3;
	}

}
