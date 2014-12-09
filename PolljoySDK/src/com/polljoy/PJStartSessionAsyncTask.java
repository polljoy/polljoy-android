package com.polljoy;

import org.json.JSONException;
import org.json.JSONObject;

public class PJStartSessionAsyncTask extends PJAsyncTask {
	String appId = null;
	String deviceId = null;
	String deviceModel = null;
	String osVersion = null;
	
	PJStartSessionAsyncTask(String appId, String deviceId) {
		this.methodName = "registerSession.json";
		this.TAG = "PJStartSessionAsyncTask";
		this.appId = appId;
		this.deviceId = deviceId;
	}

	PJStartSessionAsyncTask(String appId, String deviceId, String deviceModel, String osVersion) {
		this.methodName = "registerSession.json";
		this.TAG = "PJStartSessionAsyncTask";
		this.appId = appId;
		this.deviceId = deviceId;
		this.deviceModel = deviceModel;
		this.osVersion = osVersion;
	}
	
	@Override
	protected JSONObject extraParameters() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("appId", this.appId);
			jsonObject.putOpt("deviceId", this.deviceId);
			jsonObject.putOpt("deviceModel", this.deviceModel);
			jsonObject.putOpt("osVersion", this.osVersion);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
