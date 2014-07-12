package com.polljoy;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.polljoy.internal.PolljoyCore;
import com.polljoy.util.PJColorHelper;
import com.polljoy.util.PJDateHelper;

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
	@Deprecated
	int backgroundColor;
	@Deprecated
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
	@Deprecated
	String imageUrlToDisplay;
	String[] tags;

	int appUsageTime = 0;
	Hashtable<String, String> choiceUrl = null;
	String collectButtonText = null;
	int imageCornerRadius = 0;
	int level = 0;
	String pollRewardImageUrl = null;
	String prerequisiteType = null;
	String prerequisiteAnswer = null;
	String prerequisitePoll = null;
	Date sendDate = null;
	int session = 0;
	String submitButtonText = null;
	String thankyouButtonText = null;
	String virtualCurrency = null;
	PJApp app = null;
	int imageStatus;
	PJPollImageUrlSet imageUrlSetForDisplay = new PJPollImageUrlSet();

	public PJPoll(JSONObject jsonObject) {
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

		JSONObject choiceUrlJsonObject = jsonObject.optJSONObject("choiceUrl");
		this.choiceUrl = this
				.getChoiceUrlMapFromJSONObject(choiceUrlJsonObject);

		this.collectButtonText = jsonObject.optString("collectButtonText");
		this.pollRewardImageUrl = jsonObject.optString("pollRewardImageUrl");
		this.prerequisiteAnswer = jsonObject.optString("prerequisiteAnswer");
		this.prerequisitePoll = jsonObject.optString("prerequisitePoll");
		this.prerequisiteType = jsonObject.optString("prerequisiteType");
		this.virtualCurrency = jsonObject.optString("virtualCurrency");
		this.submitButtonText = jsonObject.optString("submitButtonText");
		this.thankyouButtonText = jsonObject.optString("thankyouButtonText");

		String dateString = jsonObject.optString("sendDate");
		this.sendDate = PJDateHelper.parseDateString(dateString);
		this.appUsageTime = jsonObject.optInt("appUsageTime");
		this.level = jsonObject.optInt("level");
		this.session = jsonObject.optInt("session");
		this.imageCornerRadius = jsonObject.optInt("imageCornerRadius");

		JSONObject appJsonObject = jsonObject.optJSONObject("app");
		PJApp parsedApp = new PJApp(appJsonObject);
		if (parsedApp.appId != null) {
			this.app = parsedApp;
		}
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

	Hashtable<String, String> getChoiceUrlMapFromJSONObject(
			JSONObject sourceJsonObject) {
		Hashtable<String, String> result = null;
		try {
			if (sourceJsonObject != null) {
				result = new Hashtable<String, String>();
				@SuppressWarnings("unchecked")
				Iterator<String> names = sourceJsonObject.keys();
				String device = PolljoyCore.isAmazonKindle()?"amazon":"android";
				while (names.hasNext()) {
					String name = names.next();
					JSONObject urlsJSONObject = sourceJsonObject
							.optJSONObject(name);
					String url = urlsJSONObject.optString(device);
					result.put(name, url);
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

	@Deprecated
	public int getBackgroundColor() {
		return backgroundColor;
	}

	@Deprecated
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

	public String getImageUrlToDisplay() {
		return imageUrlToDisplay;
	}

	public void setImageUrlToDisplay(String imageUrlToDisplay) {
		this.imageUrlToDisplay = imageUrlToDisplay;
	}

	public Hashtable<String, String> getChoiceUrl() {
		return choiceUrl;
	}

	public void setChoiceUrl(Hashtable<String, String> choiceUrl) {
		this.choiceUrl = choiceUrl;
	}

	public String getCollectButtonText() {
		return collectButtonText;
	}

	public void setCollectButtonText(String collectButtonText) {
		this.collectButtonText = collectButtonText;
	}

	public String getPollRewardImageUrl() {
		return pollRewardImageUrl;
	}

	public void setPollRewardImageUrl(String pollRewardImageUrl) {
		this.pollRewardImageUrl = pollRewardImageUrl;
	}

	public String getPrerequisiteAnswer() {
		return prerequisiteAnswer;
	}

	public void setPrerequisiteAnswer(String prerequisiteAnswer) {
		this.prerequisiteAnswer = prerequisiteAnswer;
	}

	public String getPrerequisitePoll() {
		return prerequisitePoll;
	}

	public void setPrerequisitePoll(String prerequisitePoll) {
		this.prerequisitePoll = prerequisitePoll;
	}

	public String getPrerequisiteType() {
		return prerequisiteType;
	}

	public void setPrerequisiteType(String prerequisiteType) {
		this.prerequisiteType = prerequisiteType;
	}

	public String getVirtualCurrency() {
		return virtualCurrency;
	}

	public void setVirtualCurrency(String virtualCurrency) {
		this.virtualCurrency = virtualCurrency;
	}

	public String getSubmitButtonText() {
		return submitButtonText;
	}

	public void setSubmitButtonText(String submitButtonText) {
		this.submitButtonText = submitButtonText;
	}

	public String getThankyouButtonText() {
		return thankyouButtonText;
	}

	public void setThankyouButtonText(String thankyouButtonText) {
		this.thankyouButtonText = thankyouButtonText;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public PJApp getApp() {
		return app;
	}

	public void setApp(PJApp app) {
		this.app = app;
	}

	public int getAppUsageTime() {
		return appUsageTime;
	}

	public void setAppUsageTime(int appUsageTime) {
		this.appUsageTime = appUsageTime;
	}

	public int getImageCornerRadius() {
		return imageCornerRadius;
	}

	public void setImageCornerRadius(int imageCornerRadius) {
		this.imageCornerRadius = imageCornerRadius;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getSession() {
		return session;
	}

	public void setSession(int session) {
		this.session = session;
	}

	public int getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(PJPollImageStatus imageStatus) {
		this.imageStatus = imageStatus.getStatusCode();
	}

	public PJPollImageUrlSet getImageUrlSetForDisplay() {
		return imageUrlSetForDisplay;
	}

	public void setImageUrlSetForDisplay(PJPollImageUrlSet imageUrlSetForDisplay) {
		this.imageUrlSetForDisplay = imageUrlSetForDisplay;
	}

	public void setImageStatus(int imageStatus) {
		this.imageStatus = imageStatus;
	}

}
