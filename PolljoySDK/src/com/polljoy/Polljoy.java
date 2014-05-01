package com.polljoy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;

import com.polljoy.PJAsyncTask.PJAsyncTaskListener;
import com.polljoy.PJPollViewActivity.PJPollViewActivityDelegate;
import com.polljoy.internal.ImageDownloader;
import com.polljoy.internal.Log;
import com.polljoy.internal.PolljoyCore;

public class Polljoy {
	public interface PolljoyDelegate {
		void PJPollNotAvailable(PJResponseStatus status);

		void PJPollIsReady(ArrayList<PJPoll> polls);

		void PJPollWillShow(PJPoll poll);

		void PJPollDidShow(PJPoll poll);

		void PJPollWillDismiss(PJPoll poll);

		void PJPollDidDismiss(PJPoll poll);

		void PJPollDidResponded(PJPoll poll);

		void PJPollDidSkipped(PJPoll poll);
	};

	public final static String PJ_SDK_NAME = "Polljoy";
	public final static String PJ_API_SANDBOX_endpoint = "http://api.sandbox.polljoy.com/poll/";
	public final static String PJ_API_endpoint = "http://api.polljoy.com/poll/";
	static boolean _isRegisteringSession = false;
	static boolean _needsAutoShow = false;

	static PolljoyDelegate _delegate;

	static Context _appContext;
	static String _appId;
	static String _userId;
	static String _sessionId;
	static String _deviceId;
	static String _deviceModel;
	static String _devicePlatform;
	static String _deviceOS;

	static PJApp _app;
	static PJPoll _currentPoll;

	static String _appVersion;
	static int _level;
	static int _session;
	static int _timeSinceInstall;
	static PJUserType _userType;
	static ArrayList<PJPoll> _polls = new ArrayList<PJPoll>();

	static boolean _autoShow;
	static boolean _isSandboxMode = false;

	// Android only
	public final static String TAG = "Polljoy";
	static PJStartSessionAsyncTask _startSessionTask = null;
	static PJGetPollAsyncTask _getPollTask = null;
	static PJResponsePollAsyncTask _responsePollAsyncTask = null;
	static ImageDownloader _imageDownloader = new ImageDownloader();
	static PJPollViewActivityDelegate _pollViewActivityDelegate = new PJPollViewActivityDelegate() {

		@Override
		public void PJPollViewDidAnswered(PJPollViewActivity pollView,
				PJPoll poll) {
			String response = poll.response;
			int pollToken = poll.pollToken;
			Polljoy.responsePoll(pollToken, response);
			if (poll.virtualAmount > 0) {
				pollView.showActionAfterResponse();
			} else {
				PJPoll matchedPoll = getPollWithToken(pollToken);
				_polls.remove(matchedPoll);
				if (_polls.size() > 0) {
					pollView.finish();
					showPoll();
				} else {
					pollView.showActionAfterResponse();
				}
			}

		}

		@Override
		public void PJPollViewDidSkipped(PJPollViewActivity pollView,
				PJPoll poll) {
			// post response as userSkipped
			String response = "";
			int pollToken = poll.pollToken;
			Polljoy.responsePoll(pollToken, response);
			if (_delegate != null) {
				_delegate.PJPollDidSkipped(poll);
			}
			pollView.finish();
			PJPoll matchedPoll = getPollWithToken(poll.pollToken);
			_polls.remove(matchedPoll);
			if (_polls.size() > 0) {
				showPoll();
			}
		}

		@Override
		public void PJPollViewCloseAfterReponse(PJPollViewActivity pollView,
				PJPoll poll) {
			PJPoll matchedPoll = getPollWithToken(poll.pollToken);
			_polls.remove(matchedPoll);
			if (_polls.size() > 0) {
				if (poll.virtualAmount > 0) {
					if (_delegate != null) {
						_delegate.PJPollDidResponded(poll);
					}
				}
				if (_delegate != null) {
					_delegate.PJPollWillDismiss(poll);
				}
				pollView.finish();
				if (_delegate != null) {
					_delegate.PJPollDidDismiss(poll);
				}

				showPoll();
			} else {
				if (_delegate != null) {
					_delegate.PJPollDidResponded(poll);
				}
				if (_delegate != null) {
					_delegate.PJPollWillDismiss(poll);
				}
				pollView.finish();
				if (_delegate != null) {
					_delegate.PJPollDidDismiss(poll);
				}
			}

		}

	};

	public static String getAPIEndpoint() {
		return _isSandboxMode ? PJ_API_SANDBOX_endpoint : PJ_API_endpoint;
	}

	public static void startSession(Context context, final String appId) {
		startSession(context, appId, true);
	}

	public static void startSession(Context context, final String appId,
			boolean newSession) {
		if (appId == null) {
			Log.e(TAG, "missing appId");
			return;
		}
		_appContext = context;
		_sessionId = null;
		_deviceId = PolljoyCore.getDeviceId(_appContext);
		_deviceModel = PolljoyCore.getDeviceModel();
		_devicePlatform = "android";
		_deviceOS = Build.VERSION.RELEASE;
		_appId = appId;
		_appVersion = "0";
		_level = 0;
		_userType = PJUserType.PJNonPayUser;
		_session = newSession ? PolljoyCore.getNewSession(_appContext)
				: PolljoyCore.getCurrentSession(_appContext);
		_timeSinceInstall = (int) PolljoyCore.getTimeSinceInstall(_appContext);

		_startSessionTask = new PJStartSessionAsyncTask(_appId, _deviceId);
		_startSessionTask.taskListener = new PJAsyncTaskListener() {

			@Override
			public void taskCompletedCallback(JSONObject jsonObject) {
				try {
					int status;
					status = jsonObject.getInt("status");
					if (status == PJResponseStatus.PJSuccess.statusCode()) {
						JSONObject appJson = jsonObject.getJSONObject("app");
						PJApp app = new PJApp(appJson);
						_app = app;
						_sessionId = appJson.optString("sessionId");
						_userId = appJson.optString("userId");
						if (_app.defaultImageUrl != null
								&& !_app.defaultImageUrl.equals("null")) {
							downloadAppImage(_app.defaultImageUrl);
						}
						Log.v(TAG, "startSession " + "_sessionId: "
								+ _sessionId);
						Log.v(TAG, "startSession " + "_deviceId: " + _deviceId);
						Log.v(TAG, "startSession " + "_deviceModel: "
								+ _deviceModel);
						Log.v(TAG, "startSession " + "_deviceOS: " + _deviceOS);
						Log.v(TAG, "startSession " + "_devicePlatform: "
								+ _devicePlatform);
						Log.v(TAG,
								"startSession " + "_session: "
										+ String.valueOf(_session));
						Log.v(TAG, "startSession " + "_timeSinceInstall: "
								+ String.valueOf(_timeSinceInstall));
						Log.v(TAG, "startSession " + "App: " + _app.appName);
					} else {
						Log.e(TAG, Polljoy.PJ_SDK_NAME + ": Error - Status: "
								+ String.valueOf(status));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				_startSessionTask = null;
			}

			@Override
			public void taskFailedCallback(Exception e) {
				Log.e(TAG,
						Polljoy.PJ_SDK_NAME + ":startSession Error: "
								+ e.getMessage() + " " + _appId);
				_startSessionTask = null;
			}

		};
		_startSessionTask.execute();
	}

	public static void getPoll() {
		_needsAutoShow = true;
		getPoll(null);
	}

	public static void getPoll(PolljoyDelegate delegate) {
		getPoll(null, 0, 0, 0, PJUserType.PJNonPayUser, delegate);
	}

	public static void getPoll(String appVersion, int level,
			PJUserType userType, PolljoyDelegate delegate) {
		getPoll(appVersion, level, 0, 0, userType, delegate);
	}

	static Handler schedulePollRequestHandler = null;

	static void schedulePollRequest() {
		Log.i(TAG, "schedulePollRequest");
		getPoll(_appVersion, _level, _session, _timeSinceInstall, _userType,
				_delegate);
	}

	public static void getPoll(String appVersion, int level, int session,
			int timeSinceInstall, PJUserType userType, PolljoyDelegate delegate) {
		level = Math.max(level, 0);
		session = Math.max(session, 0);
		timeSinceInstall = Math.max(timeSinceInstall, 0);

		if (_sessionId == null) {
			_appVersion = appVersion;
			_level = level;
			_session = session;
			_timeSinceInstall = timeSinceInstall;
			_userType = userType;
			_delegate = delegate;
			// check if _isRegitseringSession. if yes, delay the request by 1
			// sec
			if (_startSessionTask != null) {
				Log.i(TAG,
						"_isRegisteringSession, delay poll request for 1 sec");
				if (schedulePollRequestHandler != null) {
					schedulePollRequestHandler.removeCallbacksAndMessages(null);
				}
				schedulePollRequestHandler = new Handler();
				schedulePollRequestHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						schedulePollRequest();
					}
				}, 1 * 1000);
			} else if (_appId != null) {
				Log.i(TAG,
						"user already set appId. startSession onbehalf. delay poll request for 2 sec");
				startSession(_appContext, _appId, false);
				if (schedulePollRequestHandler != null) {
					schedulePollRequestHandler.removeCallbacksAndMessages(null);
				}
				schedulePollRequestHandler = new Handler();
				schedulePollRequestHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						schedulePollRequest();
					}
				}, 2 * 1000);
			} else {
				Log.e(TAG, PJ_SDK_NAME + ": Error - Session Not Registered");
				if (delegate != null) {
					delegate.PJPollNotAvailable(PJResponseStatus.PJNoPollFound);
				}
			}
			return;
		}
		_delegate = delegate;
		_polls = new ArrayList<PJPoll>();
		if (appVersion == null) {
			appVersion = _appVersion;
		}
		if (level <= 0) {
			level = _level;
		}
		if (timeSinceInstall <= 0) {
			timeSinceInstall = _timeSinceInstall;
		}
		if (session <= 0) {
			session = _session;
		}
		_getPollTask = new PJGetPollAsyncTask(_sessionId, _deviceId,
				_deviceModel, _devicePlatform, _deviceOS, appVersion, level,
				session, timeSinceInstall, userType);
		_getPollTask.taskListener = new PJAsyncTaskListener() {

			@Override
			public void taskCompletedCallback(JSONObject jsonObject) {
				try {
					int status;
					status = jsonObject.getInt("status");
					String message = jsonObject.optString("message");
					if (status == PJResponseStatus.PJSuccess.statusCode()) {
						JSONArray pollsJsonArray = jsonObject
								.getJSONArray("polls");
						for (int i = 0; i < pollsJsonArray.length(); i++) {
							try {
								JSONObject pollRequest = (JSONObject) pollsJsonArray
										.get(i);
								JSONObject pollJsonObject = pollRequest
										.optJSONObject("PollRequest");
								PJPoll poll = new PJPoll(pollJsonObject);
								PJPoll matchedPoll = getPollWithToken(poll.pollToken);
								if (matchedPoll != null) {
									_polls.remove(matchedPoll);
								}
								_polls.add(poll);

							} catch (NullPointerException e) {
								e.printStackTrace();
							}
						}
						for (PJPoll poll : _polls) {
							downloadPollImage(poll);
						}
					} else {
						Log.e(TAG, Polljoy.PJ_SDK_NAME + ": Error - Status: "
								+ String.valueOf(status) + " (" + message + ")");
						if (_delegate != null) {
							_delegate.PJPollNotAvailable(PJResponseStatus
									.responseStatusForCode(status));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				_getPollTask = null;
			}

			@Override
			public void taskFailedCallback(Exception e) {
				Log.e(TAG,
						Polljoy.PJ_SDK_NAME + ":getPoll Error: "
								+ e.getMessage() + " " + _appId);
				_getPollTask = null;
			}

		};
		_getPollTask.execute();
	}

	public static void responsePoll(long pollToken, String response) {
		_responsePollAsyncTask = new PJResponsePollAsyncTask(_sessionId,
				_deviceId, response, pollToken);
		_responsePollAsyncTask.taskListener = new PJAsyncTaskListener() {

			@Override
			public void taskCompletedCallback(JSONObject jsonObject) {
				try {
					int status;
					status = jsonObject.getInt("status");
					String message = jsonObject.optString("message");
					if (status == PJResponseStatus.PJSuccess.statusCode()) {
						String virtualAmount = jsonObject
								.optString("virtualAmount");
						String responseString = jsonObject
								.optString("response");
						Log.v(TAG, "status: " + status + " message: " + message);
						Log.v(TAG, "response: " + responseString);
						Log.v(TAG, "virtualAmount: " + virtualAmount);
					} else {
						Log.e(TAG, Polljoy.PJ_SDK_NAME + ": Error - Status: "
								+ status + " (" + message + ")");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				_responsePollAsyncTask = null;
			}

			@Override
			public void taskFailedCallback(Exception e) {
				Log.e(TAG,
						Polljoy.PJ_SDK_NAME + ":responsePoll Error: "
								+ e.getMessage() + " " + _appId);
				_responsePollAsyncTask = null;
			}

		};
		_responsePollAsyncTask.execute();
	}

	public static void checkPollStatus() {
		try {
			if (_polls.size() < 1) {
				return;
			}
			boolean pollsAreReady = true;
			for (PJPoll poll : _polls) {
				if (!poll.isReadyToShow) {
					pollsAreReady = false;
				}
			}
			if (pollsAreReady) {
				Log.i(TAG, "Polls are ready.");
				if (_delegate != null) {
					_delegate.PJPollIsReady(_polls);
				}
				if (_autoShow || _needsAutoShow) {
					Polljoy.showPoll();
				}
			} else {
				Log.i(TAG, "Polls are not ready.");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return;
	}

	static void downloadAppImage(String imageUrl) {
		if (imageUrl != null) {
			_imageDownloader.download(imageUrl, null,
					new ImageDownloader.CompletionHandler() {

						@Override
						public void imageDownloaded(Bitmap bitmap) {
							Log.v(TAG, "downloadAppImage completed");
						}
					});
		}
		return;
	}

	static void downloadPollImage(final PJPoll poll) {
		if (poll == null) {
			return;
		}
		String imageUrl = null;
		try {
			if (poll.pollImageUrl != null && !poll.pollImageUrl.equals("null")) {
				imageUrl = poll.pollImageUrl;
			} else if (poll.appImageUrl != null
					&& !poll.appImageUrl.equals("null")) {
				imageUrl = poll.appImageUrl;
			}
			if (imageUrl != null) {
				final String urlToDownload = imageUrl;
				poll.imageUrlToDisplay = null;
				_imageDownloader.download(urlToDownload, null,
						new ImageDownloader.CompletionHandler() {

							@Override
							public void imageDownloaded(Bitmap bitmap) {
								Log.v(TAG, "downloadPollImage completed");
								if (bitmap != null) {
									poll.imageUrlToDisplay = urlToDownload;
								} else {
									// leave imageUrlToDisplay null
								}
								poll.isReadyToShow = true;
								checkPollStatus();
							}
						});
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			poll.isReadyToShow = true;
			checkPollStatus();
		}
	}

	public static void showPoll() {
		if (_polls != null && _polls.size() > 0) {
			PJPoll poll = _polls.get(0);
			showPoll(poll);
		} else {
			if (_delegate != null) {
				_delegate.PJPollNotAvailable(PJResponseStatus.PJNoPollFound);
			}
		}
	}

	static void showPoll(PJPoll poll) {
		if (_delegate != null) {
			_delegate.PJPollWillShow(poll);
		}

		Intent intent = new Intent(_appContext,
				com.polljoy.PJPollViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("Poll", poll);
		_appContext.startActivity(intent);

		if (_delegate != null) {
			_delegate.PJPollDidShow(poll);
		}
	}

	static void delayShowPoll() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				showPoll();
			}

		}, 100);
	}

	static PJPoll getPollWithToken(int pollToken) {
		for (PJPoll poll : _polls) {
			if (poll.pollToken == pollToken) {
				return poll;
			}
		}
		return null;
	}

	public static PolljoyDelegate getDelegate() {
		return _delegate;
	}

	public static void setDelegate(PolljoyDelegate delegate) {
		_delegate = delegate;
	}

	public static Context getAppContext() {
		return _appContext;
	}

	public static void setAppContext(Context appContext) {
		_appContext = appContext;
	}

	public static String getAppId() {
		return _appId;
	}

	public static void setAppId(String appId) {
		_appId = appId;
	}

	public static String getUserId() {
		return _userId;
	}

	public static void setUserId(String userId) {
		_userId = userId;
	}

	public static String getSessionId() {
		return _sessionId;
	}

	public static void setSessionId(String sessionId) {
		_sessionId = sessionId;
	}

	public static String getDeviceId() {
		return _deviceId;
	}

	public static void setDeviceId(String deviceId) {
		_deviceId = deviceId;
	}

	public static String getDeviceModel() {
		return _deviceModel;
	}

	public static void setDeviceModel(String deviceModel) {
		_deviceModel = deviceModel;
	}

	public static String getDevicePlatform() {
		return _devicePlatform;
	}

	public static void setDevicePlatform(String devicePlatform) {
		_devicePlatform = devicePlatform;
	}

	public static String getDeviceOS() {
		return _deviceOS;
	}

	public static void setDeviceOS(String deviceOS) {
		_deviceOS = deviceOS;
	}

	public static PJApp getApp() {
		return _app;
	}

	public static void setApp(PJApp app) {
		_app = app;
	}

	public static PJPoll getCurrentPoll() {
		return _currentPoll;
	}

	public static void setCurrentPoll(PJPoll currentPoll) {
		_currentPoll = currentPoll;
	}

	public static String getAppVersion() {
		return _appVersion;
	}

	public static void setAppVersion(String appVersion) {
		_appVersion = appVersion;
	}

	public static int getLevel() {
		return _level;
	}

	public static void setLevel(int level) {
		_level = level;
	}

	public static int getSession() {
		return _session;
	}

	public static void setSession(int session) {
		_session = session;
	}

	public static int getTimeSinceInstall() {
		return _timeSinceInstall;
	}

	public static void setTimeSinceInstall(int timeSinceInstall) {
		_timeSinceInstall = timeSinceInstall;
	}

	public static PJUserType getUserType() {
		return _userType;
	}

	public static void setUserType(PJUserType userType) {
		_userType = userType;
	}

	public static ArrayList<PJPoll> getPolls() {
		return _polls;
	}

	public static void setAutoShow(boolean autoShow) {
		_autoShow = autoShow;
	}

	public static void setSandboxMode(boolean sandboxMode) {
		_isSandboxMode = sandboxMode;
	}

	public static PJPollViewActivityDelegate getPollViewActivityDelegate() {
		return _pollViewActivityDelegate;
	}

	public static ImageDownloader getImageDownloader() {
		return _imageDownloader;
	}
}
