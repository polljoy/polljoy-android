package com.polljoy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.polljoy.internal.Log;

public abstract class PJAsyncTask extends AsyncTask<Void, Void, JSONObject> {
	public interface PJAsyncTaskListener {
		public void taskCompletedCallback(JSONObject jsonObject);

		public void taskFailedCallback(Exception e);
	}

	String TAG = "PJAsyncTask";
	String methodName = "";
	PJAsyncTaskListener taskListener = null;
	Exception failureException = null;

	@Override
	protected JSONObject doInBackground(Void... params) {
		int requestStatusCode = -1;
		JSONObject jsonObject = null;
		try {

			String method = Polljoy.getAPIEndpoint() + this.methodName;
			HttpPost httpRequest = new HttpPost(method);
			ArrayList<NameValuePair> nameValuePairs = prepareNameValuePairs();
			Log.i(TAG, "method: " + method);
			Log.i(TAG, "request entity: " + nameValuePairs.toString());
			httpRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			requestStatusCode = httpResponse.getStatusLine().getStatusCode();
			Log.i(TAG, String.format("requestStatusCode code = %d",
					requestStatusCode));
			if (requestStatusCode == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				Log.i(TAG, "result = " + result);
				jsonObject = new JSONObject(result);
			} else {
				String responsePhrase = httpResponse.getStatusLine()
						.getReasonPhrase();
				this.failureException = new PJException(responsePhrase);
			}
		} catch (NullPointerException e) {
			exceptionCatched(e);
		} catch (UnsupportedEncodingException e) {
			exceptionCatched(e);
		} catch (ClientProtocolException e) {
			exceptionCatched(e);
		} catch (IOException e) {
			exceptionCatched(e);
		} catch (JSONException e) {
			exceptionCatched(e);
		}
		return jsonObject;
	}

	protected ArrayList<NameValuePair> prepareNameValuePairs() {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			JSONObject jsonObject = extraParameters();
			ArrayList<NameValuePair> nameValuePairsFromJsonObject = getNameValuePairs(jsonObject);
			nameValuePairs.addAll(nameValuePairsFromJsonObject);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return nameValuePairs;
	}

	protected JSONObject extraParameters() {
		return null;
	}

	private void exceptionCatched(Exception e) {
		e.printStackTrace();
		this.failureException = e;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (this.failureException != null) {
			if (this.taskListener != null) {
				this.taskListener.taskFailedCallback(this.failureException);
			}
		} else {
			if (this.taskListener != null) {
				this.taskListener.taskCompletedCallback(result);
			}
		}
	}

	public static Map<String, Object> getMap(JSONObject jsonObject) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIterator.hasNext()) {
				key = (String) keyIterator.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<NameValuePair> getNameValuePairs(
			JSONObject jsonObject) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = jsonObject.keys();
			String key;
			String value;
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			while (keyIterator.hasNext()) {
				key = (String) keyIterator.next();
				value = jsonObject.getString(key);
				nameValuePairs.add(new BasicNameValuePair(key, value));
			}
			return nameValuePairs;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
