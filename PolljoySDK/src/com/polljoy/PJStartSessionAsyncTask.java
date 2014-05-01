package com.polljoy;

import org.json.JSONException;
import org.json.JSONObject;

public class PJStartSessionAsyncTask extends PJAsyncTask {
	String appId = null;
	String deviceId = null;

	PJStartSessionAsyncTask(String appId, String deviceId) {
		this.methodName = "registerSession.json";
		this.TAG = "PJStartSessionAsyncTask";
		this.appId = appId;
		this.deviceId = deviceId;
	}

	@Override
	protected JSONObject extraParameters() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("appId", this.appId);
			jsonObject.putOpt("deviceId", this.deviceId);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
