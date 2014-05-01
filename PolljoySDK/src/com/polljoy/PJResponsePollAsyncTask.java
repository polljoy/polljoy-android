package com.polljoy;

import org.json.JSONException;
import org.json.JSONObject;

public class PJResponsePollAsyncTask extends PJAsyncTask {
	String sessionId = null;
	String deviceId = null;
	String response = null;

	PJResponsePollAsyncTask(String sessionId, String deviceId, String response, long tokenId) {
		this.methodName = String.format("response/%d.json", tokenId);
		this.TAG = "PJResponsePollAsyncTask";
		this.sessionId = sessionId;
		this.deviceId = deviceId;
		this.response = response;
	}

	@Override
	protected JSONObject extraParameters() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("sessionId", this.sessionId);
			jsonObject.putOpt("deviceId", this.deviceId);
			jsonObject.putOpt("response", this.response);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
