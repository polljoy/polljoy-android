package com.polljoy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.polljoy.PJAsyncTask.PJAsyncTaskListener;
import com.polljoy.PJFileDownloadAsyncTask.PJFileDownloadAsyncTaskListener;
import com.polljoy.PJPollViewActivity.PJPollViewActivityDelegate;
import com.polljoy.internal.Log;
import com.polljoy.internal.PolljoyCore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

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
	public final static String PJ_API_SANDBOX_endpoint = "https://apisandbox.polljoy.com/3.0/poll/";
	public final static String PJ_API_endpoint = "https://api.polljoy.com/3.0/poll/";
	static String _SDKVersion = "3.0.1";
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
    static boolean _activeMau;
    static boolean _mauLimitReached = false;

	static PJApp _app;
	static PJPoll _currentPoll;

	static String _appVersion;
	static int _level;
	static int _session;
	static int _timeSinceInstall;
	static PJUserType _userType;
	static String _tags;
	static ArrayList<PJPoll> _polls = new ArrayList<PJPoll>();

	static boolean _autoShow;
	static boolean _isSandboxMode = false;

	static MediaPlayer customSound = null;
    static MediaPlayer customTapSound = null;
	static double _messageShowDuration = 1.5;
	static PJRewardThankyouMessageStyle _rewardThankyouMessageStyle = PJRewardThankyouMessageStyle.PJRewardThankyouMessageStyleMessage ;
    static int _getPollRetryCount = 0;
    static int _maxGetPollRetryCount = 20;

	// Android only
	static PJScreenType _screenType;
	public final static String TAG = "Polljoy";
	static PJStartSessionAsyncTask _startSessionTask = null;
	static PJGetPollAsyncTask _getPollTask = null;
	static PJResponsePollAsyncTask _responsePollAsyncTask = null;
	static PJFileDownloadAsyncTask _customSoundDownloadAsyncTask = null;
	static PJFileDownloadAsyncTask _customTapSoundDownloadAsyncTask = null;
	static int _currentShowingPollToken = Integer.MIN_VALUE;
	static ArrayList<Target> imageCacheTargets = new ArrayList<Target>();
	public final static int BORDER_IMAGE_MAX_LENGTH = 800;

	static PJPollViewActivityDelegate _pollViewActivityDelegate = new PJPollViewActivityDelegate() {

		@Override
		public void PJPollViewDidAnswered(PJPollViewActivity pollView,
				PJPoll poll) {
			String response = poll.response;
			int pollToken = poll.pollToken;
			Polljoy.responsePoll(pollToken, response);	
			
		    // check if virtual reward answer assigned
		    if (poll.virtualRewardAnswer != null && !poll.virtualRewardAnswer.equals("null") && poll.virtualRewardAnswer.length() > 0) {		
		    	if (!poll.virtualRewardAnswer.equals(response)) poll.virtualAmount = 0 ;  // no reward if response not equal to reward answer
		    }
		    
			if (poll.virtualAmount > 0) {
				pollView.showActionAfterResponse();
			} else {
				PJPoll matchedPoll = getPollWithToken(pollToken);
				_polls.remove(matchedPoll);
			
				if (poll.childPolls != null) {
			        PJPoll childPoll = null;
			        
			        Hashtable<String, Object> childPolls = poll.childPolls;
			        if (childPolls.get(response) != null) {
			            childPoll = (PJPoll) childPolls.get(response);
			        }
			        else  if (childPolls.get("polljoyPollAnyAnswer") != null) {
			            childPoll = (PJPoll) childPolls.get("polljoyPollAnyAnswer");
			        }
			        
			        if (childPoll != null) {
			        	_polls.add(0, childPoll);
			        }
			    }

				if (_polls.size() > 0) {
					if (_delegate != null) {
						_delegate.PJPollDidResponded(poll);
					}
					if (_delegate != null) {
						_delegate.PJPollWillDismiss(poll);
					}
                    pollView.playTapSound();
					pollView.finish();
					if (_delegate != null) {
						_delegate.PJPollWillDismiss(poll);
					}
					
					_currentShowingPollToken = Integer.MIN_VALUE;
					showPoll();
				} else {
					pollView.showActionAfterResponse();
				}
			}
			
			if ((_rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStylePopup) || 
				((_rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStyleMessage) && (_polls.size() > 0))	
				){
				if (poll.type.equals("M") || poll.type.equals("I")) {
					String url = poll.choiceUrl.get(response);
					if (url != null) {
						try {
							Uri uri = Uri.parse(url);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							pollView.startActivity(intent);
						} catch (android.content.ActivityNotFoundException e) {
							Log.d(TAG, "Url: " + url);
							Log.d(TAG,
									"ActivityNotFoundException: "
											+ e.getLocalizedMessage());
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
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
			_currentShowingPollToken = Integer.MIN_VALUE;
			PJPoll matchedPoll = getPollWithToken(poll.pollToken);
			_polls.remove(matchedPoll);
			if (_polls.size() > 0) {
				showPoll();
			}
		}

		@Override
		public void PJPollViewCloseAfterResponse(PJPollViewActivity pollView,
                                                 PJPoll poll) {
			PJPoll matchedPoll = getPollWithToken(poll.pollToken);
			_polls.remove(matchedPoll);
			
			String response = poll.response;
			if (poll.childPolls != null) {
		        PJPoll childPoll = null;
		        
		        Hashtable<String, Object> childPolls = poll.childPolls;
		        if (childPolls.get(response) != null) {
		            childPoll = (PJPoll) childPolls.get(response);
		        }
		        else  if (childPolls.get("polljoyPollAnyAnswer") != null) {
		            childPoll = (PJPoll) childPolls.get("polljoyPollAnyAnswer");
		        }
		        
		        if (childPoll != null) {
		        	_polls.add(0, childPoll);
		        }
		    }
			
			_currentShowingPollToken = Integer.MIN_VALUE;
			if (_polls.size() > 0) {
				if (_delegate != null) {
					_delegate.PJPollDidResponded(poll);
				}
				if (_delegate != null) {
					_delegate.PJPollWillDismiss(poll);
				}
				pollView.finish();
				if (_delegate != null) {
					_delegate.PJPollWillDismiss(poll);
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

			if (_rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStyleMessage) {
				if (poll.type.equals("M") || poll.type.equals("I")) {
					String url = poll.choiceUrl.get(response);
					Log.d(TAG, "Url: " + url);
					if (url != null) {
						try {
							Uri uri = Uri.parse(url);
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							pollView.startActivity(intent);
						} catch (android.content.ActivityNotFoundException e) {
							Log.d(TAG, "Url: " + url);
							Log.d(TAG,
									"ActivityNotFoundException: "
											+ e.getLocalizedMessage());
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

	};

	@SuppressLint("NewApi")
	private static void executeTask(PJAsyncTask task) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			task.execute();
	}

    private static void executeDownloadTask(PJFileDownloadAsyncTask task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

	public static String getAPIEndpoint() {
		return _isSandboxMode ? PJ_API_SANDBOX_endpoint : PJ_API_endpoint;
	}

	public static void startSession(Context context, final String appId) {
		startSession(context, appId, null, true);
	}

    public static void startSession(Context context, final String appId, final String deviceId) {
        startSession(context, appId, deviceId, true);
    }

    public static void startSession(Context context, final String appId, final String deviceId,
			boolean newSession) {
		if (appId == null) {
			Log.e(TAG, "missing appId");
			return;
		}
		_appContext = context;
		Picasso.with(_appContext).setLoggingEnabled(Log.loggingEnabled);
		WindowManager windowManager = (WindowManager) _appContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point screenSize = PJScreenConfiguration.getRealSizeForDisplay(display);
		_screenType = PJScreenType.screenTypeForScreenSize(screenSize);
		_sessionId = null;
        if (deviceId == null) {
            _deviceId = PolljoyCore.getDeviceId(_appContext);
        }
        else {
            _deviceId = deviceId;
        }
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
		_startSessionTask = new PJStartSessionAsyncTask(_appId, _deviceId, _deviceModel, _devicePlatform + " " + _deviceOS);
		_startSessionTask.taskListener = new PJAsyncTaskListener() {

			@Override
			public void taskCompletedCallback(JSONObject jsonObject) {
				try {
					int status;
					status = jsonObject.getInt("status");
					if (status == PJResponseStatus.PJSuccess.statusCode()) {
                        JSONObject sessionJson = jsonObject.getJSONObject("session");
						_sessionId = sessionJson.optString("sessionId");
                        _mauLimitReached = jsonObject.optBoolean("mauLimitReached");
                        _activeMau = sessionJson.optBoolean("activeMau");
						_userId = sessionJson.optString("userId");
						Log.i(TAG, "startSession " + "_sessionId: "
								+ _sessionId);
						Log.i(TAG, "startSession " + "_deviceId: " + _deviceId);
						Log.i(TAG, "startSession " + "_deviceModel: "
								+ _deviceModel);
						Log.i(TAG, "startSession " + "_deviceOS: " + _deviceOS);
						Log.i(TAG, "startSession " + "_devicePlatform: "
								+ _devicePlatform);
						Log.i(TAG,
								"startSession " + "_session: "
										+ String.valueOf(_session));
						Log.i(TAG, "startSession " + "_timeSinceInstall: "
								+ String.valueOf(_timeSinceInstall));
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
		executeTask(_startSessionTask);
	}

	public static void getPoll() {
		_needsAutoShow = true;
		getPoll(null);
	}

	public static void getPoll(PolljoyDelegate delegate) {
		getPoll(null, _level, _session, _timeSinceInstall, PJUserType.PJNonPayUser, delegate);
	}

	public static void getPoll(String appVersion, int level,
			PJUserType userType, PolljoyDelegate delegate) {
		getPoll(appVersion, level, _session, _timeSinceInstall, userType, delegate);
	}

	public static void getPoll(String appVersion, int level, int session,
			int timeSinceInstall, PJUserType userType, PolljoyDelegate delegate) {
		getPoll(appVersion, level, session, timeSinceInstall, userType, null,
				delegate);
	}

	static Handler schedulePollRequestHandler = null;

	static void schedulePollRequest() {
		Log.i(TAG, "schedulePollRequest");
		getPoll(_appVersion, _level, _session, _timeSinceInstall, _userType,
				_tags, _delegate);
        _getPollRetryCount++;
	}

	public static void getPoll(String appVersion, int level, int session,
			int timeSinceInstall, PJUserType userType, String tags,
			PolljoyDelegate delegate) {
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
			_tags = tags;
			// check if _isRegitseringSession. if yes, delay the request by 2
			// sec
			if (_startSessionTask != null) {
				Log.i(TAG,
						"_isRegisteringSession, delay poll request for 2 sec");
				if (schedulePollRequestHandler != null) {
					schedulePollRequestHandler.removeCallbacksAndMessages(null);
				}
                if (_getPollRetryCount < _maxGetPollRetryCount) {
                    schedulePollRequestHandler = new Handler();
                    schedulePollRequestHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            schedulePollRequest();
                        }
                    }, 2 * 1000);
                }
                else {
                    Log.e(TAG, PJ_SDK_NAME + ": Error - Giving up. Retry " + _getPollRetryCount + "times but still cannot getPoll");
                    _getPollRetryCount = 0;
                    if (delegate != null) {
                        delegate.PJPollNotAvailable(PJResponseStatus.PJNoPollFound);
                    }
                }
			} else if (_appId != null && !_mauLimitReached) {
				Log.i(TAG,
						"user already set appId. startSession on behalf. delay poll request for 2 sec");
				startSession(_appContext, _appId, _deviceId, false);
				if (schedulePollRequestHandler != null) {
					schedulePollRequestHandler.removeCallbacksAndMessages(null);
				}
                if (_getPollRetryCount < _maxGetPollRetryCount) {
                    schedulePollRequestHandler = new Handler();
                    schedulePollRequestHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            schedulePollRequest();
                        }
                    }, 2 * 1000);
                }
                else {
                    Log.e(TAG, PJ_SDK_NAME + ": Error - Giving up. Retry " + _getPollRetryCount + " times but still cannot getPoll");
                    _getPollRetryCount = 0;
                    if (delegate != null) {
                        delegate.PJPollNotAvailable(PJResponseStatus.PJNoPollFound);
                    }
                }
			} else if (_mauLimitReached) {
                Log.e(TAG, PJ_SDK_NAME + "MAU Limit Reached");
                _getPollRetryCount = 0;
                if (delegate != null) {
                    delegate.PJPollNotAvailable(PJResponseStatus.PJNoPollFound);
                }
            } else {
				Log.e(TAG, PJ_SDK_NAME + ": Error - Session Not Registered");
                _getPollRetryCount = 0;
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
				session, timeSinceInstall, userType, tags);
		_getPollTask.taskListener = new PJAsyncTaskListener() {

			@Override
			public void taskCompletedCallback(JSONObject jsonObject) {
				try {
					int status;
					status = jsonObject.getInt("status");
					String message = jsonObject.optString("message");
					if (status == PJResponseStatus.PJSuccess.statusCode()) {
						JSONArray pollsJsonArray = jsonObject.getJSONArray("polls");

                        // parse app object
                        JSONObject appJson = jsonObject.getJSONObject("app");
                        PJApp app = new PJApp(appJson);
                        _app = app;
                        downloadCustomSound();
                        downloadCustomTapSound();
                        //downloadAppImages();

                        int count = 0;
						for (int i = 0; i < pollsJsonArray.length(); i++) {
							try {

								JSONObject pollRequest = (JSONObject) pollsJsonArray
										.get(i);
								JSONObject pollJsonObject = pollRequest
										.optJSONObject("PollRequest");
								PJPoll poll = new PJPoll(pollJsonObject,_app);
								PJPoll matchedPoll = getPollWithToken(poll.pollToken);
								if (matchedPoll != null) {
									_polls.remove(matchedPoll);
								}
								_polls.add(poll);

								count++;
							} catch (NullPointerException e) {
								e.printStackTrace();
							}
						}
						for (PJPoll poll : _polls) {
							downloadPollImages(poll);
							
							if (poll.childPolls != null) {
								downloadChildPollImages(poll.childPolls);
							}
						}
						Log.d(TAG, "Polls:" + _polls.toString());
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
		executeTask(_getPollTask);
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
						String virtualCurrency = jsonObject
								.optString("virtualCurrency");
						String responseString = jsonObject
								.optString("response");
						Log.i(TAG, "status: " + status + " message: " + message);
						Log.i(TAG, "response: " + responseString);
						Log.i(TAG, "virtualAmount: " + virtualAmount);
						Log.i(TAG, "virtualCurrency: " + virtualCurrency);
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
		executeTask(_responsePollAsyncTask);
	}

	public static void checkPollImagesStatus(PJPoll poll) {
		if (!poll.isReadyToShow) {
			Log.d(TAG,
					"checkPollImagesStatus, poll("
							+ String.valueOf(poll.pollId) + ") imageStatus = "
							+ poll.imageStatus);
			if (PJPollImageStatus.PJPollAllImageReady.getStatusCode() == poll.imageStatus) {
                if (poll.type.equals("I") && poll.imagePollStatus < poll.choices.length) {
                    return;
                }
				poll.isReadyToShow = true;
				Log.i(TAG, "Poll(" + String.valueOf(poll.pollId)
						+ ") isReadyToShow");
				checkPollStatus();
			}
		}
	}

	public static void checkPollStatus() {
		try {
			Log.d(TAG, "checkPollStatus");
			if (_polls.size() < 1) {
				return;
			}		
			boolean pollsAreReady = true;
			for (PJPoll poll : _polls) {
				if (!poll.isReadyToShow) {
					pollsAreReady = false;
					break;
				}
				else {
					pollsAreReady = checkChildPollStatus(poll.childPolls);	
				}
			}
			if (pollsAreReady) {
				Log.i(TAG, "Polls are ready. numOfPolls = " + _polls.size());
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

	public static boolean checkChildPollStatus(Hashtable<String, Object> childPolls) {
		boolean pollsAreReady = true;
		Log.i(TAG,"Check Child Polls images");
	    if (childPolls != null) {
	    	for (String key : childPolls.keySet()) {
			    PJPoll poll = (PJPoll) childPolls.get(key);
	            
	            if (!poll.isReadyToShow) {
					pollsAreReady = false;
					break;
				}
				else {
					pollsAreReady = checkChildPollStatus(poll.childPolls);
				}
	            
	            Log.i(TAG,"Child Polls are " + (pollsAreReady?"ready":"not ready") + " for pollId:" + Integer.toString(poll.pollId));
	        }
	    }
	    
	    return pollsAreReady;
	}
	
	// ImageUrl handling

	static boolean isUrlValid(String url) {
		if (url != null && !url.equals("null") && url.length() > 0) {
			return true;
		}
		return false;
	}

	static void downloadAppImages() {
		PJPollImageUrlSet imageUrlSet = _app
				.imageUrlSetForScreenType(_screenType);
		downloadAppImage(imageUrlSet.pollImageUrl, "defaultImageUrl");
		downloadAppImage(imageUrlSet.rewardImageUrl, "rewardImageUrl");
		downloadAppImage(imageUrlSet.borderImageL, "borderImageL",
				BORDER_IMAGE_MAX_LENGTH);
		downloadAppImage(imageUrlSet.borderImageP, "borderImageP",
				BORDER_IMAGE_MAX_LENGTH);
		downloadAppImage(imageUrlSet.buttonImageL, "buttonImageL");
		downloadAppImage(imageUrlSet.buttonImageP, "buttonImageP");
	}

	static void downloadCustomSound() {
		downloadCustomSound(_app.customSoundUrl, "customSound");
	}

    static void downloadCustomTapSound() {
        downloadCustomSound(_app.customTapSoundUrl, "customTapSound");
    }
	synchronized static void addCacheTarget(Target target) {
		imageCacheTargets.add(target);
	}

	synchronized static void removeCacheTarget(Target target) {
		imageCacheTargets.remove(target);
	}

	static void downloadAppImage(String imageUrl, final String name) {
		downloadAppImage(imageUrl, name, 0);
	}

	static void downloadAppImage(final String imageUrl, final String name,
			int maxLength) {
		if (isUrlValid(imageUrl)) {
			Target target = new Target() {
				@Override
				public void onBitmapFailed(Drawable arg0) {
					Log.i(TAG, "downloadAppImage: download " + name + " failed");
					removeCacheTarget(this);
				}

				@Override
				public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
					Log.i(TAG, "downloadAppImage: download " + name
							+ " completed");

                    String cacheFilename = PolljoyCore.createFilenameFromUrl(_appContext, imageUrl, "png");
                    File cacheFile = new File(cacheFilename);
                    if (!cacheFile.exists()) {
                        PolljoyCore.saveImageCache(arg0,cacheFilename);
                    }

					removeCacheTarget(this);
				}

				@Override
				public void onPrepareLoad(Drawable arg0) {
				}
			};
			addCacheTarget(target);

            String cacheFilename = PolljoyCore.createFilenameFromUrl(_appContext, imageUrl, "png");
            File cacheFile = new File(cacheFilename);
            RequestCreator request = null;
            Log.d(TAG, name + ": cacheFilename " + cacheFilename);
            if (cacheFile.exists()) {
                Log.d(TAG, "load from cache:  " + name
                        + " " + imageUrl);
                request = Picasso.with(_appContext).load(cacheFile);
            }
            else {
                Log.d(TAG, "load from network:  " + name
                        + " " + imageUrl);
                request = Picasso.with(_appContext).load(imageUrl);
            }

			if (maxLength > 0) {
				request.resize(maxLength, maxLength).centerInside();
			}
			request.into(target);
		}
		return;
	}

	static void downloadPollImages(final PJPoll poll) {
		if (poll == null) {
			return;
		}

		downloadPollImage("pollImage", poll.pollImageUrl, poll.appImageUrl,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
						poll.imageUrlSetForDisplay.pollImageUrlSource = source;
						poll.imageUrlSetForDisplay.pollImageUrl = downloadedUrl;
						boolean isUsingFallbackUrl = !(downloadedUrl != null && downloadedUrl
								.equals(poll.pollImageUrl));
						PJPollImageUrlSet defaultImageUrlSet = _app
								.imageUrlSetForScreenType(_screenType);
						if (isUsingFallbackUrl) {
							poll.imageUrlSetForDisplay.pollImageCornerRadius = defaultImageUrlSet.pollImageCornerRadius;
						} else {
							poll.imageUrlSetForDisplay.pollImageCornerRadius = poll.imageCornerRadius;
						}
						poll.imageStatus |= PJPollImageStatus.PJPollDefaultImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		downloadPollImage("pollRewardImageUrl", poll.pollRewardImageUrl,
				poll.app.rewardImageUrl,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
						poll.imageUrlSetForDisplay.rewardImageUrlSource = source;
						poll.imageUrlSetForDisplay.rewardImageUrl = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollRewardImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		downloadPollImage("closeButtonImageUrl", poll.app.closeButtonImageUrl,
				null, new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
						poll.imageUrlSetForDisplay.closeButtonImageUrlSource = source;
						poll.imageUrlSetForDisplay.closeButtonImageUrl = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollCloseButtonImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		PJPollImageUrlSet imageUrlSet = _app
				.imageUrlSetForScreenType(_screenType);
		downloadPollImage("borderImageL", imageUrlSet.borderImageL, null,
				BORDER_IMAGE_MAX_LENGTH,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
                        poll.imageUrlSetForDisplay.borderImageLSource = source;
						poll.imageUrlSetForDisplay.borderImageL = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollBorderLImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		downloadPollImage("borderImageP", imageUrlSet.borderImageP, null,
				BORDER_IMAGE_MAX_LENGTH,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
                        poll.imageUrlSetForDisplay.borderImagePSource = source;
						poll.imageUrlSetForDisplay.borderImageP = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollBorderPImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		downloadPollImage("buttonImageL", imageUrlSet.buttonImageL, null,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
						poll.imageUrlSetForDisplay.buttonImageLSource = source;
						poll.imageUrlSetForDisplay.buttonImageL = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollButtonLImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});
		downloadPollImage("buttonImageP", imageUrlSet.buttonImageP, null,
				new PollImageDownloadingCompletionHandler() {
					@Override
					public void imageDownloadedForUrl(String downloadedUrl, String source) {
						poll.imageUrlSetForDisplay.buttonImagePSource = source;
						poll.imageUrlSetForDisplay.buttonImageP = downloadedUrl;
						poll.imageStatus |= PJPollImageStatus.PJPollButtonPImageReady
								.getStatusCode();
						checkPollImagesStatus(poll);
					}
				});

        if (poll.type.equals("I")) {
            for (String imagePollChoice: poll.choices) {
                String imagePollUrl = poll.choiceImageUrl.get(imagePollChoice);
                Log.i(TAG, "image poll url is: " + imagePollUrl);
                downloadPollImage("imagePollImages", imagePollUrl, null,
                        new PollImageDownloadingCompletionHandler() {
                            @Override
                            public void imageDownloadedForUrl(String downloadedUrl, String source) {
                                poll.imagePollStatus++;
                                Log.i(TAG, "image poll url is: " + downloadedUrl + "/ source: " + source);
                                poll.choiceImageUrlSource.put(downloadedUrl, source);
                                checkPollImagesStatus(poll);
                            }
                        });
                }
        }
	}

	public interface PollImageDownloadingCompletionHandler {
		void imageDownloadedForUrl(String downloadedUrl, String source);
	}

	static void downloadPollImage(final String name, final String imageUrl,
			final String fallbackImageUrl, int maxLength,
			final PollImageDownloadingCompletionHandler completionHandler) {
		String url = null;
		try {
			if (isUrlValid(imageUrl)) {
				url = imageUrl;
			} else if (isUrlValid(fallbackImageUrl)) {
				url = fallbackImageUrl;
			}
			if (url != null) {
				final String urlToDownload = url;
				Target target = new Target() {
					@Override
					public void onBitmapFailed(Drawable arg0) {
						Log.i(TAG, "downloadPollImage: download " + name
								+ " failed");
						completionHandler.imageDownloadedForUrl(null, "DISK");
						removeCacheTarget(this);
					}

					@Override
					public void onBitmapLoaded(Bitmap arg0, LoadedFrom arg1) {
						Log.i(TAG, "downloadPollImage: download " + name
								+ " completed. From: " + arg1.toString());

                        if (arg1.toString().equals("NETWORK")) {
                            String cacheFilename = PolljoyCore.createFilenameFromUrl(_appContext, urlToDownload, "png");
                            File cacheFile = new File(cacheFilename);

                            Log.d(TAG, "downloadPollImage: saving cache file: " + cacheFilename
                                    + " url: " + urlToDownload);

                            // try to reload from disk cache, tweet for Picasso bug in loading large disk file
                            Picasso.with(_appContext).load(cacheFile).into(new Target() {
                                @Override
                                public void onBitmapFailed(Drawable arg0) {
                                }

                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                                }

                                @Override
                                public void onPrepareLoad(Drawable arg0) {
                                }
                            });

                            PolljoyCore.saveImageCache(arg0,cacheFilename);
                        }

                        completionHandler.imageDownloadedForUrl(urlToDownload, arg1.toString());
                        removeCacheTarget(this);

					}

					@Override
					public void onPrepareLoad(Drawable arg0) {
					}
				};
				addCacheTarget(target);

                String cacheFilename = PolljoyCore.createFilenameFromUrl(_appContext, urlToDownload, "png");
                File cacheFile = new File(cacheFilename);
                RequestCreator request = null;
                Log.d(TAG, name + ": cacheFilename " + cacheFilename);

                if (cacheFile.exists()) {
                    Log.d(TAG, "load from cache:  " + name
                            + " " + urlToDownload);
                    request = Picasso.with(_appContext).load(cacheFile);
                }
                else {
                    Log.d(TAG, "load from network:  " + name
                            + " " + urlToDownload);
                    request = Picasso.with(_appContext).load(urlToDownload);
                }

				if (maxLength > 0) {
					request.resize(maxLength, maxLength).centerInside();
				}
				request.into(target);
			} else {
				completionHandler.imageDownloadedForUrl(null, "DISK");
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			if (completionHandler != null) {
				completionHandler.imageDownloadedForUrl(null, "DISK");
			}
		}
	}

	static void downloadPollImage(final String name, final String imageUrl,
			final String fallbackImageUrl,
			final PollImageDownloadingCompletionHandler completionHandler) {
		downloadPollImage(name, imageUrl, fallbackImageUrl, 0,
				completionHandler);
	}

	static void downloadChildPollImages(final Hashtable<String, Object> childPolls) {
		for (String key : childPolls.keySet()) {
		    PJPoll poll = (PJPoll) childPolls.get(key);
		    downloadPollImages(poll);
		    if (poll.childPolls != null) {
		    	downloadChildPollImages(poll.childPolls);
		    }
		}
	}

	static void downloadCustomSound(final String soundUrl, final String type) {
        Log.d(TAG, type + " download started");
		if (isUrlValid(soundUrl)) {

            String cacheFilename = PolljoyCore.createFilenameFromUrl(_appContext, soundUrl, "mp3");
            File cacheFile = new File(cacheFilename);
            Log.d(TAG, type + ": cacheFilename " + cacheFilename);

            if (cacheFile.exists()) {
                Log.d(TAG, " load from cache:  " + type
                        + " " + soundUrl);
                prepareCustomSound(cacheFilename, type);
            }
            else {
                Log.d(TAG, " load from network:  " + type
                        + " " + soundUrl);

                final PJFileDownloadAsyncTask fileDownloadAsyncTask;

                if (type.equals("customSound")) {
                    _customSoundDownloadAsyncTask = new PJFileDownloadAsyncTask(soundUrl, cacheFilename);
                    fileDownloadAsyncTask = _customSoundDownloadAsyncTask;
                }
                else {
                    _customTapSoundDownloadAsyncTask = new PJFileDownloadAsyncTask(soundUrl, cacheFilename);
                    fileDownloadAsyncTask = _customTapSoundDownloadAsyncTask;
                }

                fileDownloadAsyncTask.taskListener = new PJFileDownloadAsyncTaskListener() {

                    @Override
                    public void taskCompletedCallback(String filename) {
                        Log.d(TAG, type + " download completed. url: " + soundUrl + " cache filename : " + filename);
                        try {
                            prepareCustomSound(filename, type);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (type.equals("customSound")) {
                            _customSoundDownloadAsyncTask = null;
                        }
                        else {
                            _customTapSoundDownloadAsyncTask = null;
                        }
                    }

                    @Override
                    public void taskFailedCallback(Exception e) {
                        Log.e(TAG, Polljoy.PJ_SDK_NAME + ": " + type + " download Error: "
                                        + e.getMessage() + " " + _appId);
                        if (type.equals("customSound")) {
                            _customSoundDownloadAsyncTask = null;
                        }
                        else {
                            _customTapSoundDownloadAsyncTask = null;
                        }
                    }
                };
                executeDownloadTask(fileDownloadAsyncTask);
            }
	    }
	}

    static void prepareCustomSound(String dataSource, final String type) {
        Log.d(TAG, "prepareCustomSound type: " + type + " source: " + dataSource);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(dataSource);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (type.equals("customSound")) {
                    customSound = mp;
                    customSound.setLooping(false);
                }
                else if (type.equals("customTapSound")) {
                    customTapSound = mp;
                    customTapSound.setLooping(false);
                }

                Log.d(TAG, type + " is ready");
                //customSound.start();
            }
        });
        mediaPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

	public static void showPoll() {
		if (_currentShowingPollToken != Integer.MIN_VALUE) {
			return;
		}
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
		_currentShowingPollToken = poll.pollToken;
		Intent intent = new Intent(_appContext,
				com.polljoy.PJPollViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_FROM_BACKGROUND);
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

	// setters and getters
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

	public static String getTags() {
		return _tags;
	}

	public static void setTags(String tags) {
		_tags = tags;
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

	public static String getSDKVersion() {
		return _SDKVersion;
	}

	public static void setMessageShowDuration (double seconds){
	    _messageShowDuration = seconds;
	}

	public static void setRewardThankyouMessageStyle (PJRewardThankyouMessageStyle style) {
	    _rewardThankyouMessageStyle = style;
	}

}
