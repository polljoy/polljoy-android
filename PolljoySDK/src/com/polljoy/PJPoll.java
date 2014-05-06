package com.polljoy;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.polljoy.util.PJColorHelper;

public class PJPoll implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3572245274796827161L;
	String appId;
	int pollId;
	int desiredResponses;
	boolean active;
	int totalResponses;
	String pollText;
	String type;
	String priority;
	@Deprecated
	String choice;
	String[] choices;
	boolean randomOrder;
	boolean mandatory;
	int virtualAmount;
	String userType;
	String pollPlatform;
	String versionStart;
	String versionEnd;
	int levelStart;
	int levelEnd;
	int sessionStart;
	int sessionEnd;
	int timeSinceInstallStart;
	int timeSinceInstallEnd;
	String customMessage;
	String pollImageUrl;
	int userId;
	String appImageUrl;
	int backgroundColor;
	int borderColor;
	int buttonColor;
	int fontColor;
	int maximumPollPerSession = 0;
	int maximumPollPerDay = 0;
	int maximumPollInARow = 0;
	String sessionId;
	String platform;
	String osVersion;
	String deviceId;
	int pollToken;
	String response;
	boolean isReadyToShow;
	String imageUrlToDisplay;
	String[] tags;

	PJPoll(JSONObject jsonObject) {
		this.appId = jsonObject.optString("appId");
		this.pollId = jsonObject.optInt("pollId");
		this.desiredResponses = jsonObject.optInt("desiredResponses");
		this.totalResponses = jsonObject.optInt("totalResponses");
		this.active = jsonObject.optBoolean("active");
		this.pollText = jsonObject.optString("pollText");
		this.type = jsonObject.optString("type");
		this.priority = jsonObject.optString("priority");
		this.choice = jsonObject.optString("choice");
		this.randomOrder = jsonObject.optBoolean("randomOrder");
		this.mandatory = jsonObject.optBoolean("mandatory");
		this.virtualAmount = jsonObject.optInt("virtualAmount");
		this.userType = jsonObject.optString("userType");
		this.pollPlatform = jsonObject.optString("pollPlatform");
		this.versionStart = jsonObject.optString("versionStart");

		this.versionEnd = jsonObject.optString("versionEnd");
		this.levelStart = jsonObject.optInt("levelStart");
		this.levelEnd = jsonObject.optInt("levelEnd");
		this.sessionStart = jsonObject.optInt("sessionStart");
		this.sessionEnd = jsonObject.optInt("sessionEnd");
		this.timeSinceInstallStart = jsonObject.optInt("timeSinceInstallStart");
		this.timeSinceInstallEnd = jsonObject.optInt("timeSinceInstallEnd");
		this.customMessage = jsonObject.optString("customMessage");

		this.pollImageUrl = jsonObject.optString("pollImageUrl");
		this.userId = jsonObject.optInt("userId");
		this.appImageUrl = jsonObject.optString("appImageUrl");

		this.backgroundColor = PJColorHelper.colorForString(jsonObject
				.optString("backgroundColor"));
		this.borderColor = PJColorHelper.colorForString(jsonObject
				.optString("borderColor"));
		this.buttonColor = PJColorHelper.colorForString(jsonObject
				.optString("buttonColor"));
		this.fontColor = PJColorHelper.colorForString(jsonObject
				.optString("fontColor"));

		this.maximumPollPerSession = jsonObject.optInt("maxPollsPerSession");
		this.maximumPollPerDay = jsonObject.optInt("maxPollsPerDay");
		this.maximumPollInARow = jsonObject.optInt("maxPollsInARow");

		this.sessionId = jsonObject.optString("sessionId");
		this.platform = jsonObject.optString("platform");
		this.osVersion = jsonObject.optString("osVersion");
		this.pollToken = jsonObject.optInt("pollToken");
		this.response = jsonObject.optString("response");
		this.isReadyToShow = false;
		JSONArray choicesJsonArray = jsonObject.optJSONArray("choices");
		this.choices = convertJSONArrayToStringArray(choicesJsonArray);
		JSONArray tagsJsonArray = jsonObject.optJSONArray("tags");
		this.tags = convertJSONArrayToStringArray(tagsJsonArray);
		this.imageUrlToDisplay = null;
	}

	String[] convertJSONArrayToStringArray(JSONArray jsonArray) {
		String[] result = null;
		try {
			if (jsonArray != null && jsonArray.length() > 0) {
				result = new String[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					String aChoice = jsonArray.optString(i);
					result[i] = aChoice;
				}
			} else {
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getPollId() {
		return pollId;
	}

	public void setPollId(int pollId) {
		this.pollId = pollId;
	}

	public int getDesiredResponses() {
		return desiredResponses;
	}

	public void setDesiredResponses(int desiredResponses) {
		this.desiredResponses = desiredResponses;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getTotalResponses() {
		return totalResponses;
	}

	public void setTotalResponses(int totalResponses) {
		this.totalResponses = totalResponses;
	}

	public String getPollText() {
		return pollText;
	}

	public void setPollText(String pollText) {
		this.pollText = pollText;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Deprecated
	public String getChoice() {
		return choice;
	}

	@Deprecated
	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String[] getChoices() {
		return choices;
	}

	public void setChoices(String[] choices) {
		this.choices = choices;
	}

	public boolean isRandomOrder() {
		return randomOrder;
	}

	public void setRandomOrder(boolean randomOrder) {
		this.randomOrder = randomOrder;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public int getVirtualAmount() {
		return virtualAmount;
	}

	public void setVirtualAmount(int virtualAmount) {
		this.virtualAmount = virtualAmount;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPollPlatform() {
		return pollPlatform;
	}

	public void setPollPlatform(String pollPlatform) {
		this.pollPlatform = pollPlatform;
	}

	public String getVersionStart() {
		return versionStart;
	}

	public void setVersionStart(String versionStart) {
		this.versionStart = versionStart;
	}

	public String getVersionEnd() {
		return versionEnd;
	}

	public void setVersionEnd(String versionEnd) {
		this.versionEnd = versionEnd;
	}

	public int getLevelStart() {
		return levelStart;
	}

	public void setLevelStart(int levelStart) {
		this.levelStart = levelStart;
	}

	public int getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(int levelEnd) {
		this.levelEnd = levelEnd;
	}

	public int getSessionStart() {
		return sessionStart;
	}

	public void setSessionStart(int sessionStart) {
		this.sessionStart = sessionStart;
	}

	public int getSessionEnd() {
		return sessionEnd;
	}

	public void setSessionEnd(int sessionEnd) {
		this.sessionEnd = sessionEnd;
	}

	public int getTimeSinceInstallStart() {
		return timeSinceInstallStart;
	}

	public void setTimeSinceInstallStart(int timeSinceInstallStart) {
		this.timeSinceInstallStart = timeSinceInstallStart;
	}

	public int getTimeSinceInstallEnd() {
		return timeSinceInstallEnd;
	}

	public void setTimeSinceInstallEnd(int timeSinceInstallEnd) {
		this.timeSinceInstallEnd = timeSinceInstallEnd;
	}

	public String getCustomMessage() {
		return customMessage;
	}

	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}

	public String getPollImageUrl() {
		return pollImageUrl;
	}

	public void setPollImageUrl(String pollImageUrl) {
		this.pollImageUrl = pollImageUrl;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAppImageUrl() {
		return appImageUrl;
	}

	public void setAppImageUrl(String appImageUrl) {
		this.appImageUrl = appImageUrl;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getPollToken() {
		return pollToken;
	}

	public void setPollToken(int pollToken) {
		this.pollToken = pollToken;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isReadyToShow() {
		return isReadyToShow;
	}

	public void setReadyToShow(boolean isReadyToShow) {
		this.isReadyToShow = isReadyToShow;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}
}
