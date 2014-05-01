package com.polljoy;

import org.json.JSONException;
import org.json.JSONObject;

public class PJGetPollAsyncTask extends PJAsyncTask {
	String sessionId = null;
	String deviceId = null;
	String deviceModel = null;
	String devicePlatform = null;
	String deviceOS = null;
	String appVersion = null;
	int level = 0;
	int sessionCount = 0;
	int timeSinceInstall = 0;
	PJUserType userType = PJUserType.PJNonPayUser;

	PJGetPollAsyncTask(String sessionId, String deviceId, String deviceModel,
			String devicePlatform, String deviceOS, String appVersion,
			int level, int sessionCount, int timeSinceInstall,
			PJUserType userType) {
		this.methodName = "smartget.json";
		this.TAG = "PJGetPollAsyncTask";

		this.sessionId = sessionId;
		this.deviceId = deviceId;
		this.deviceModel = deviceModel;
		this.devicePlatform = devicePlatform;
		this.deviceOS = deviceOS;
		this.appVersion = appVersion;
		this.level = level;
		this.sessionCount = sessionCount;
		this.timeSinceInstall = timeSinceInstall;
		this.userType = userType;
	}

	@Override
	protected JSONObject extraParameters() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("sessionId", this.sessionId);
			jsonObject.putOpt("deviceId", this.deviceId);
			jsonObject.putOpt("deviceModel", this.deviceModel);
			jsonObject.putOpt("platform", this.devicePlatform);
			jsonObject.putOpt("osVersion", this.deviceOS);
			jsonObject.putOpt("deviceModel", this.deviceModel);
			jsonObject.putOpt("appVersion", this.appVersion);
			if (this.level > 0) {
				jsonObject.putOpt("level", this.level);
			}
			if (this.sessionCount > 0) {
				jsonObject.putOpt("sessionCount", this.sessionCount);
			}
			if (this.timeSinceInstall > 0) {
				jsonObject.putOpt("timeSinceInstall", this.timeSinceInstall);
			}
			jsonObject.putOpt("timeSinceInstall", this.timeSinceInstall);
			jsonObject.putOpt("userType", this.userType.userTypeString());
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
