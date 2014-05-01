package com.polljoy;

import org.json.JSONObject;

import com.polljoy.util.PJColorHelper;

public class PJApp {
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

	PJApp(JSONObject jsonObject) {
		this.appId = jsonObject.optString("appId");
		this.appName = jsonObject.optString("appName");
		this.defaultImageUrl = jsonObject.optString("defaultImageUrl");
		this.maximumPollPerSession = jsonObject.optInt("maxPollsPerSession");
		this.maximumPollPerDay = jsonObject.optInt("maxPollsPerDay");
		this.maximumPollInARow = jsonObject.optInt("maxPollsInARow");
		this.backgroundColor = PJColorHelper.colorForString(jsonObject.optString("backgroundColor"));
		this.borderColor = PJColorHelper.colorForString(jsonObject.optString("borderColor"));
		this.buttonColor = PJColorHelper.colorForString(jsonObject.optString("buttonColor"));
		this.fontColor = PJColorHelper.colorForString(jsonObject.optString("fontColor"));
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

}
