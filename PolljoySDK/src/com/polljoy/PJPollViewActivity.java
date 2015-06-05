package com.polljoy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout.Alignment;
//import android.util.DisplayMetrics;
//import android.util.Log;
import android.util.TypedValue;
import android.text.format.*;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.polljoy.internal.AutofitTextView;
import com.polljoy.internal.ImageTextButton;
import com.polljoy.internal.Log;
import com.polljoy.internal.PolljoyCore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;

public class PJPollViewActivity extends Activity {
	public interface PJPollViewActivityDelegate {
		void PJPollViewDidAnswered(PJPollViewActivity pollView, PJPoll poll);

		void PJPollViewDidSkipped(PJPollViewActivity pollView, PJPoll poll);

		void PJPollViewCloseAfterResponse(PJPollViewActivity pollView,
                                          PJPoll poll);
	}

	private PJPoll myPoll;
	PJPollViewActivityDelegate delegate;
	View fullScreenOverlay;
	View backgroundColorOverlay;
	ImageView borderImageOverlay;
	ImageView pollImageView;
	ImageView virtualAmountImageView;
    View imagePollLayout;
    ImageTextButton imagePollMain;
    ImageTextButton imagePoll1;
    ImageTextButton imagePoll2;
    ImageTextButton imagePoll3;
    ImageTextButton imagePoll4;
    Button imagePollConfirm;
    int selectedImagePoll;

	View pollView;
	TextView questionTextView;
	TextView virtualAmountTextView;
	TextView virtualAmountRewardTextView;
	EditText responseEditText;
	View mcButtonsLayout;
	ImageTextButton mcButton1;
	ImageTextButton mcButton2;
	ImageTextButton mcButton3;
	ImageTextButton mcButton4;
	View textResponseLayout;
	ImageTextButton submitButton;
	ImageTextButton collectButton;
	ImageButton closeButton;
	ImageView rewardImageView;
	TextView rewardAmountTextView;
	View offerLayout;
	View userRespondedLayout;
	View userInteractionLayout;
	View pollImageLayout;
	View responseRewardsLayout;
	boolean userResponded = false;
	String responseText = null;
	BitmapDrawable borderImageDrawable;

	protected ImageTextButton answeredButton;
	protected boolean enabled = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myPoll = (PJPoll) this.getIntent().getExtras().get("Poll");
		this.delegate = Polljoy.getPollViewActivityDelegate();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			requestWindowActionModeOverlay();
		}
		setupViews();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void requestWindowActionModeOverlay() {
		requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (textResponseLayout.getVisibility() == View.VISIBLE) {
			responseEditText.setFocusable(true);
			responseEditText.setFocusableInTouchMode(true);
			responseEditText.requestFocus();
			responseEditText.requestFocusFromTouch();
		}
	}

	@Override
	public void onBackPressed() {
		if (myPoll.isMandatory()) {
			return;
		} else {
			userSkipped(null);
		}
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (!myPoll.isMandatory() && myPoll.app.closeButtonEasyClose) {
            if (checkTouchOutside(event)== true){
                userSkipped(null);
            }
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (enabled)
            return super.dispatchTouchEvent(ev);
        return true;
    }
    
    private boolean checkTouchOutside (MotionEvent event) {
        if ( event.getRawX() < this.pollView.getLeft()){
            return true;
        }
        if ( event.getRawX() > this.pollView.getRight() ){
            return true;
        }
        if (event.getRawY() < this.pollView.getTop()) {
            return true;
        }
        if (event.getRawY() > this.pollView.getBottom()) {
            return true;
        }
        return false;
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.responseText = this.responseEditText.getText().toString();
		setupViews();
	}

	void setupViews() {
		this.setContentView(R.layout.activity_poll_view);
		this.fullScreenOverlay = (View) findViewById(R.id.pollViewLayout);
		this.pollView = (View) findViewById(R.id.pollView);
		this.pollImageView = (ImageView) findViewById(R.id.pollImageView);
        if(!myPoll.getType().equals("I")) {
            this.offerLayout = (View) findViewById(R.id.offerLayout);
            this.virtualAmountImageView = (ImageView) findViewById(R.id.virtualAmountImageView);
            this.virtualAmountTextView = (TextView) findViewById(R.id.virtualAmountTextView);
            this.virtualAmountRewardTextView = (TextView) findViewById(R.id.virtualAmountRewardTextView);
        }else {
            this.offerLayout = (View) findViewById(R.id.offerLayout2);
            this.virtualAmountImageView = (ImageView) findViewById(R.id.virtualAmountImageView2);
            this.virtualAmountTextView = (TextView) findViewById(R.id.virtualAmountTextView2);
            this.virtualAmountRewardTextView = (TextView) findViewById(R.id.virtualAmountRewardTextView2);
        }
		this.questionTextView = (TextView) findViewById(R.id.questionTextView);
		this.mcButtonsLayout = (View) findViewById(R.id.mcButtonsLayout);
		this.mcButton1 = (ImageTextButton) findViewById(R.id.mcButton1);
        this.mcButton1.setSoundEffectsEnabled(false);
		this.mcButton2 = (ImageTextButton) findViewById(R.id.mcButton2);
        this.mcButton2.setSoundEffectsEnabled(false);
		this.mcButton3 = (ImageTextButton) findViewById(R.id.mcButton3);
        this.mcButton3.setSoundEffectsEnabled(false);
		this.mcButton4 = (ImageTextButton) findViewById(R.id.mcButton4);
        this.mcButton4.setSoundEffectsEnabled(false);
		ImageTextButton[] mcButtons = { mcButton1, mcButton2, mcButton3,
				mcButton4 };

		this.textResponseLayout = (View) findViewById(R.id.textResponseLayout);
		this.responseEditText = (EditText) findViewById(R.id.responseEditText);
		this.submitButton = (ImageTextButton) findViewById(R.id.submitButton);
        this.submitButton.setSoundEffectsEnabled(false);
		this.collectButton = (ImageTextButton) findViewById(R.id.collectButton);
        this.collectButton.setSoundEffectsEnabled(false);
		this.closeButton = (ImageButton) findViewById(R.id.closeButton);
        this.closeButton.setSoundEffectsEnabled(false);
		this.backgroundColorOverlay = (View) findViewById(R.id.backgroundColorOverlay);
		this.borderImageOverlay = (ImageView) findViewById(R.id.borderImageOverlay);
		this.rewardImageView = (ImageView) findViewById(R.id.rewardImageView);
		this.rewardAmountTextView = (TextView) findViewById(R.id.rewardAmountTextView);

		this.userRespondedLayout = (View) findViewById(R.id.userRespondedLayout);
		this.responseRewardsLayout = (View) findViewById(R.id.responseRewardsLayout);
		this.userInteractionLayout = (View) findViewById(R.id.userInteractionLayout);
		this.pollImageLayout = (View) findViewById(R.id.pollImageLayout);

        this.imagePollLayout = (View) findViewById(R.id.imagePollLayout);
        this.imagePollMain = (ImageTextButton) findViewById(R.id.imagePollMainImage);
        this.imagePollMain.setSoundEffectsEnabled(false);
        this.imagePoll1 = (ImageTextButton) findViewById(R.id.imagePoll1);
        this.imagePoll1.setSoundEffectsEnabled(false);
        this.imagePoll2 = (ImageTextButton) findViewById(R.id.imagePoll2);
        this.imagePoll2.setSoundEffectsEnabled(false);
        this.imagePoll3 = (ImageTextButton) findViewById(R.id.imagePoll3);
        this.imagePoll3.setSoundEffectsEnabled(false);
        this.imagePoll4 = (ImageTextButton) findViewById(R.id.imagePoll4);
        this.imagePoll4.setSoundEffectsEnabled(false);

        OnClickListener changeImageListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImagePoll((ImageTextButton) v);
            }

        };
        ImageTextButton[] imagePollImages = {this.imagePoll1,this.imagePoll2,this.imagePoll3,this.imagePoll4};
        for(ImageTextButton currentImage: imagePollImages) {
            currentImage.setOnClickListener(changeImageListener);
        }
        this.imagePollConfirm = (Button) findViewById(R.id.imagePollConfirm);
        this.imagePollConfirm.setSoundEffectsEnabled(false);

        OnClickListener imageClickListener= new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImagePoll((ImageTextButton) v);
            }
        };
        imagePollMain.setOnClickListener(imageClickListener);
        
        OnClickListener confirmImageListener= new OnClickListener() {
            @Override
            public void onClick(View v) {
                userResponded(imagePollMain);
            }
        };
        this.imagePollConfirm.setOnClickListener(confirmImageListener);


		int orientation = this.getResources().getConfiguration().orientation;
		PJScreenConfiguration screenConfig = new PJScreenConfiguration(this,
				orientation);

		int innerWidth = (int) screenConfig.innerWidth;
		int innerHeight = (int) screenConfig.innerHeight;
		int borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				this.myPoll.app.borderWidth, this.getResources()
						.getDisplayMetrics());
		this.adjustLayoutSize(this.pollView, innerWidth, innerHeight);
		this.adjustLayoutSize(this.backgroundColorOverlay, innerWidth
				+ borderWidth * 2, innerHeight + borderWidth * 2);

		OnTouchListener getFocusListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						responseEditText.getWindowToken(), 0);
				return false;
			}
		};
		this.fullScreenOverlay.setOnTouchListener(getFocusListener);
		this.borderImageOverlay.setOnTouchListener(getFocusListener);
		this.backgroundColorOverlay.setOnTouchListener(getFocusListener);

		int overlayAlpha = (100 - myPoll.app.overlayAlpha) * 255 / 100;
		GradientDrawable overlayDrawable = new GradientDrawable();
		overlayDrawable.setColor(getResources().getColor(R.color.pj_black));
		overlayDrawable.setAlpha(overlayAlpha);
		this.setDrawableForView(this.fullScreenOverlay, overlayDrawable);

		int alpha = myPoll.app.backgroundAlpha * 255 / 100;
		GradientDrawable backgroundDrawable = (GradientDrawable) this
				.getResources().getDrawable(R.drawable.poll_view_border);
		backgroundDrawable.setStroke(borderWidth, myPoll.app.borderColor);
		backgroundDrawable.setColor(myPoll.app.backgroundColor);
		backgroundDrawable.setAlpha(alpha);
		float cornerRadius = myPoll.app.backgroundCornerRadius;
		backgroundDrawable.setCornerRadius(cornerRadius);
		this.setDrawableForView(this.backgroundColorOverlay, backgroundDrawable);

		this.questionTextView.setTextColor(myPoll.getFontColor());
		this.questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				screenConfig.fontSize);
		this.questionTextView.setText(myPoll.getPollText());

		this.responseEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				screenConfig.fontSize);
		this.responseEditText.setText(this.responseText);

		OnClickListener submissionListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userResponded((ImageTextButton) v);
			}

		};
		for (ImageTextButton button : mcButtons) {
			button.setOnClickListener(submissionListener);
		}
		this.submitButton.setOnClickListener(submissionListener);
		OnClickListener collectListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userConfirmed(v);
			}

		};
		this.submitButton.setText(this.myPoll.submitButtonText);

		this.collectButton.setOnClickListener(collectListener);
		OnClickListener closeButtonListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userSkipped(v);
			}

		};

		this.closeButton.setOnClickListener(closeButtonListener);
		PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(
				myPoll.app.fontColor, PorterDuff.Mode.SRC_ATOP);
		this.closeButton.setColorFilter(colorFilter);

		// reward amounts
		this.virtualAmountTextView
				.setText(String.valueOf(myPoll.virtualAmount));
		this.rewardAmountTextView.setText(String.valueOf(myPoll.virtualAmount));
		// reward text colors
		this.virtualAmountTextView.setTextColor(myPoll.app.fontColor);
		this.rewardAmountTextView.setTextColor(myPoll.app.fontColor);
		this.virtualAmountRewardTextView.setTextColor(myPoll.app.fontColor);
		// reward text sizes
		this.virtualAmountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				screenConfig.rewardFontSize);
		this.virtualAmountRewardTextView.setTextSize(
				TypedValue.COMPLEX_UNIT_PX, screenConfig.rewardFontSize);
		this.rewardAmountTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				screenConfig.fontSize); // the only special one

		this.setPollImage();
		this.setRewardImage();
		this.setCloseButtonImage();

        if (myPoll.getType().equals("I")) {
            this.setImagePoll();
        }

		this.updatePollViewState(this.userResponded);

		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			this.configurePortraitLayout(screenConfig);
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			this.configureLandscapeLayout(screenConfig);
		}
		
        imagePollMain.setTextSize(TypedValue.COMPLEX_UNIT_PX, screenConfig.fontSize);
	}

	void updatePollViewState(boolean userResponded) {
		if (!userResponded) {
			userRespondedLayout.setVisibility(View.INVISIBLE);
			this.closeButton
					.setVisibility(myPoll.isMandatory() ? View.INVISIBLE
							: View.VISIBLE);
			try {
				if (myPoll.type.equals("M")) {
					String[] choices = myPoll.choices;
					ImageTextButton[] mcButtons = { mcButton1, mcButton2,
							mcButton3, mcButton4 };
					for (ImageTextButton button : mcButtons) {
						button.setVisibility(View.INVISIBLE);
					}
					int offset = mcButtons.length - choices.length;

					for (int i = 0; i < choices.length; i++) {
						ImageTextButton button = mcButtons[i + offset];
						button.setVisibility(View.VISIBLE);
						button.setText(choices[i]);
					}
					mcButtonsLayout.setVisibility(View.VISIBLE);
					textResponseLayout.setVisibility(View.INVISIBLE);
                    imagePollLayout.setVisibility(View.INVISIBLE);
				} else if (myPoll.type.equals("T")) {
					mcButtonsLayout.setVisibility(View.INVISIBLE);
                    imagePollLayout.setVisibility(View.INVISIBLE);
                    textResponseLayout.setVisibility(View.VISIBLE);
				} else if (myPoll.type.equals("I")) {
                    mcButtonsLayout.setVisibility(View.INVISIBLE);
                    imagePollLayout.setVisibility(View.VISIBLE);

                } else {
					mcButtonsLayout.setVisibility(View.INVISIBLE);
					textResponseLayout.setVisibility(View.INVISIBLE);
                    imagePollLayout.setVisibility(View.INVISIBLE);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				mcButtonsLayout.setVisibility(View.INVISIBLE);
				textResponseLayout.setVisibility(View.INVISIBLE);
			}
			if (myPoll.virtualAmount > 0) {
				this.offerLayout.setVisibility(View.VISIBLE);
			} else {
				this.offerLayout.setVisibility(View.INVISIBLE);
			}
		} else {

			if (myPoll.virtualAmount > 0) {
				if (Polljoy._rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStylePopup) {
					mcButtonsLayout.setVisibility(View.INVISIBLE);
					textResponseLayout.setVisibility(View.INVISIBLE);
					offerLayout.setVisibility(View.INVISIBLE);
		            imagePollLayout.setVisibility(View.INVISIBLE);
					this.questionTextView.setText(myPoll.customMessage);
					closeButton.setVisibility(View.INVISIBLE);
					userRespondedLayout.setVisibility(View.VISIBLE);
					
					responseRewardsLayout.setVisibility(View.VISIBLE);
					collectButton.setText(myPoll.collectButtonText);
				}
				else {
					showCollectMsg();
				}
			} else {
				if (Polljoy._rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStylePopup) {
					mcButtonsLayout.setVisibility(View.INVISIBLE);
					textResponseLayout.setVisibility(View.INVISIBLE);
					offerLayout.setVisibility(View.INVISIBLE);
		            imagePollLayout.setVisibility(View.INVISIBLE);
					this.questionTextView.setText(myPoll.customMessage);
					closeButton.setVisibility(View.INVISIBLE);
					userRespondedLayout.setVisibility(View.VISIBLE);
					
					responseRewardsLayout.setVisibility(View.INVISIBLE);
					collectButton.setText(myPoll.thankyouButtonText);
				}
				else {
					showThankyouMsg();
				}
			}
		}
	}

	void adjustLayoutWidth(View view, int width) {
		LayoutParams parameters = view.getLayoutParams();
		this.adjustLayoutSize(view, width, parameters.height);
	}

	void adjustLayoutHeight(View view, int height) {
		LayoutParams parameters = view.getLayoutParams();
		this.adjustLayoutSize(view, parameters.width, height);
	}

	void adjustLayoutSize(View view, int width, int height) {
		LayoutParams parameters = view.getLayoutParams();
		parameters.width = width;
		parameters.height = height;
		view.setLayoutParams(parameters);
	}

	void adjustLayoutSize(View view, LayoutParams parameters, int width,
			int height) {
		parameters.width = width;
		parameters.height = height;
		view.setLayoutParams(parameters);
	}

	void adjustLayoutMargins(View view, int left, int top, int right, int bottom) {
		MarginLayoutParams parameters = (MarginLayoutParams) view
				.getLayoutParams();
		parameters.setMargins(left, top, right, bottom);
		view.setLayoutParams(parameters);
	}


	void setDrawableForView(View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setBackground(view, drawable);
		} else {
			setBackgroundDrawable(view, drawable);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void setBackground(View view, Drawable drawable) {
		view.setBackground(drawable);
	}

	@SuppressWarnings("deprecation")
	void setBackgroundDrawable(View view, Drawable drawable) {
		view.setBackgroundDrawable(drawable);
	}

	void configurePortraitLayout(PJScreenConfiguration screenConfig) {

		this.setBorderImageWithUrl(myPoll.imageUrlSetForDisplay.borderImageP, myPoll.imageUrlSetForDisplay.borderImagePSource);

		int shadowOffset = screenConfig.heightWithPercentage(1.06);

		// setup close button
		int designMargin = screenConfig.heightWithPercentage(1.875);
		int paddingForEasyTouch = 20;
		int length = screenConfig.heightWithPercentage(3.75) * 2
				+ paddingForEasyTouch * 2; 
		this.adjustLayoutSize(this.closeButton, length, length);
		int margin = designMargin - paddingForEasyTouch;
		this.closeButton.setPadding(paddingForEasyTouch, paddingForEasyTouch,
				paddingForEasyTouch, paddingForEasyTouch);
		int closeButtonOffsetX = (int) (myPoll.app.closeButtonOffsetX * screenConfig.baseScale);
		int closeButtonOffsetY = (int) (myPoll.app.closeButtonOffsetY * screenConfig.baseScale);
		if (myPoll.app.closeButtonLocation == PJCloseButtonLocation.TopLeft) {
			this.adjustLayoutMargins(this.closeButton, closeButtonOffsetX
					+ margin, closeButtonOffsetY + margin, margin, 0);
            if(!myPoll.getType().equals("I")) {
                RelativeLayout.LayoutParams parameters = (RelativeLayout.LayoutParams) this.offerLayout
                        .getLayoutParams();
                parameters.addRule(RelativeLayout.RIGHT_OF, R.id.pollImageView);
                this.offerLayout.setLayoutParams(parameters);
            }
		} else {
			this.adjustLayoutMargins(this.closeButton, margin,
					closeButtonOffsetY + margin, margin + closeButtonOffsetX, 0);
            RelativeLayout.LayoutParams parameters = (RelativeLayout.LayoutParams) this.closeButton
					.getLayoutParams();
			parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			this.closeButton.setLayoutParams(parameters);
		}

		// poll image
		length = screenConfig.heightWithPercentage(21.88);
		this.adjustLayoutWidth(this.pollImageView, length);

        //image poll
        if(myPoll.getType().equals("I")) {
            setImagePollPortraitLayout(screenConfig);
        }


		// reward offer texts
		this.adjustLayoutMargins(this.virtualAmountRewardTextView,
				screenConfig.widthWithPercentage(10), 0, 0, 0);
		int width = screenConfig.widthWithPercentage(17.78);
		int height = screenConfig.heightWithPercentage(3.125);
		this.adjustLayoutSize(this.virtualAmountRewardTextView, width, height);
		this.adjustLayoutSize(this.virtualAmountTextView, width, height);

		// reward offer icon
		length = screenConfig.heightWithPercentage(6.26);
		this.adjustLayoutSize(this.virtualAmountImageView, length, length);
		this.adjustLayoutMargins(this.virtualAmountImageView, 0, 0,
				screenConfig.widthWithPercentage(1.5), 0);

		// reward collected icon
		this.adjustLayoutSize(this.rewardImageView, length, length);

		// question text box
		int verticalMargin = screenConfig.heightWithPercentage(2.34);
		int horizontalMargin = screenConfig
				.widthWithPercentage((100 - 88.89) * 0.5);
		this.adjustLayoutMargins(this.questionTextView, horizontalMargin,
				verticalMargin, horizontalMargin, verticalMargin);

		// multiple-choice buttons
		// submit button
		int horizontalPadding = screenConfig.widthWithPercentage(88.89 - 86.67)
				+ shadowOffset;
		height = screenConfig.heightWithPercentage(10.63) + 2 * shadowOffset;
		horizontalMargin = screenConfig
				.widthWithPercentage((100 - 88.89) * 0.5) - shadowOffset;
		int marginTop = -shadowOffset;
		int marginBottom = screenConfig.heightWithPercentage(3.13)
				- shadowOffset;
		if (marginBottom < 0)
			marginBottom = 0;
		ImageTextButton[] buttons = { mcButton1, mcButton2, mcButton3,
				mcButton4, this.submitButton, this.collectButton };
		for (ImageTextButton button : buttons) {
			this.adjustLayoutHeight(button, height);
			this.adjustLayoutMargins(button, horizontalMargin, marginTop,
					horizontalMargin, marginBottom);
			button.button
					.setPadding(horizontalPadding, 0, horizontalPadding, 0);
			this.adjustLayoutMargins(button.button, shadowOffset, shadowOffset,
					shadowOffset, shadowOffset);
			this.adjustLayoutMargins(button.imageView, 2 * shadowOffset,
					2 * shadowOffset, 0, 0);
			this.setButtonStyle(button,
					myPoll.imageUrlSetForDisplay.buttonImageP, screenConfig, myPoll.imageUrlSetForDisplay.buttonImagePSource);
		}

		// open answer edit text
		horizontalMargin = screenConfig
				.widthWithPercentage((100 - 88.89) * 0.5);
		verticalMargin = screenConfig.heightWithPercentage(3.13);
		this.adjustLayoutMargins(this.responseEditText, horizontalMargin, 0,
				horizontalMargin, verticalMargin);
		int responseEditTextVerticalPadding = screenConfig
				.heightWithPercentage((38.13 - 34.31) * 0.5);
		int responseEditTextHorizontalPadding = screenConfig
				.widthWithPercentage((88.89 - 80.00) * 0.5);
		this.responseEditText.setPadding(responseEditTextHorizontalPadding,
				responseEditTextVerticalPadding,
				responseEditTextHorizontalPadding,
				responseEditTextVerticalPadding);
	}

	void configureLandscapeLayout(PJScreenConfiguration screenConfig) {
		this.setBorderImageWithUrl(myPoll.imageUrlSetForDisplay.borderImageL, myPoll.imageUrlSetForDisplay.borderImageLSource);

		int shadowOffset = screenConfig.heightWithPercentage(1.667);

		// setup close button
		int designMargin = screenConfig.heightWithPercentage(2.5);
		int paddingForEasyTouch = 20;
		int length = screenConfig.heightWithPercentage(5) * 2 + paddingForEasyTouch
				* 2;
		this.adjustLayoutSize(this.closeButton, length, length);
		int margin = designMargin - paddingForEasyTouch;
		this.closeButton.setPadding(paddingForEasyTouch, paddingForEasyTouch,
				paddingForEasyTouch, paddingForEasyTouch);

		int closeButtonOffsetX = (int) (myPoll.app.closeButtonOffsetX * screenConfig.baseScale);
		int closeButtonOffsetY = (int) (myPoll.app.closeButtonOffsetY * screenConfig.baseScale);
		if (myPoll.app.closeButtonLocation == PJCloseButtonLocation.TopLeft) {
			this.adjustLayoutMargins(this.closeButton, closeButtonOffsetX
					+ margin, margin + closeButtonOffsetY, margin, 0);
			LinearLayout landscapeCoreLayout = (LinearLayout) findViewById(R.id.landscapeCoreLayout);
			View landscapeQALayout = (View) findViewById(R.id.landscapeQALayout);
			landscapeCoreLayout.removeView(this.pollImageLayout);
			landscapeCoreLayout.removeView(landscapeQALayout);
			landscapeCoreLayout.addView(landscapeQALayout, 1);
			landscapeCoreLayout.addView(pollImageLayout, 3);
		} else {
			this.adjustLayoutMargins(this.closeButton, margin,
					closeButtonOffsetY + margin, closeButtonOffsetX + margin, 0);
			RelativeLayout.LayoutParams parameters = (RelativeLayout.LayoutParams) this.closeButton
					.getLayoutParams();
			parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			this.closeButton.setLayoutParams(parameters);
		}

		// top spacer
		int height = screenConfig.heightWithPercentage(5);
		View topSpacer = (View) findViewById(R.id.topSpacer);
		this.adjustLayoutHeight(topSpacer, height);

		// left spacer
		int leftSpacerWidth = screenConfig.heightWithPercentage(5);
		View leftSpacer = (View) findViewById(R.id.leftSpacer);
		this.adjustLayoutWidth(leftSpacer, leftSpacerWidth);

		// inner spacer
		int innerSpacerWidth = screenConfig.heightWithPercentage(3.333);
		View innerSpacer = (View) findViewById(R.id.innerSpacer);
		this.adjustLayoutWidth(innerSpacer, innerSpacerWidth);

		// right spacer
		int rightSpacerWidth = screenConfig.heightWithPercentage(3.333);
		View rightSpacer = (View) findViewById(R.id.rightSpacer);
		this.adjustLayoutWidth(rightSpacer, rightSpacerWidth);

		// pollImageLayout
		int pollImageWidth = screenConfig.heightWithPercentage(29.167);
		this.adjustLayoutWidth(this.pollImageLayout, pollImageWidth);

		// poll image
		length = screenConfig.heightWithPercentage(29.167);
		this.adjustLayoutHeight(this.pollImageView, length);

        int iconWidth = screenConfig.heightWithPercentage(13.32);
        // reward offer texts
        this.adjustLayoutMargins(this.offerLayout, 0, 0, 0,
                screenConfig.heightWithPercentage(5 + (60.833 - 12.5) / 2));
        int rewardTextHeight = screenConfig.heightWithPercentage(12.5 / 2);
        this.adjustLayoutHeight(this.virtualAmountRewardTextView,
                rewardTextHeight);

        // reward offer icon
        this.adjustLayoutSize(this.virtualAmountImageView, iconWidth, iconWidth);
        this.adjustLayoutMargins(this.virtualAmountImageView, 0, 0,
                screenConfig.heightWithPercentage(1.5), 0);


		// reward collected icon
		this.adjustLayoutSize(this.rewardImageView, iconWidth, iconWidth);

		// question text box
		height = screenConfig.heightWithPercentage(29.167);
		this.adjustLayoutHeight(this.questionTextView, height);
		int verticalMargin = screenConfig.heightWithPercentage(0);
		int horizontalMargin = screenConfig.widthWithPercentage(0)
				+ shadowOffset;
		this.adjustLayoutMargins(this.questionTextView, horizontalMargin,
				verticalMargin, horizontalMargin, verticalMargin);
		this.questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				(float) (screenConfig.fontSize * 1.2));

		// multiple-choice buttons
		// submit button
		int horizontalPadding = screenConfig.widthWithPercentage(0.1 * 66.875)
				- shadowOffset;

		height = screenConfig.heightWithPercentage(13.3) + 2 * shadowOffset;
		horizontalMargin = screenConfig.widthWithPercentage(0);
		int marginTop = -shadowOffset;
		int marginBottom = screenConfig.heightWithPercentage(2.5325)
				- shadowOffset;
		if (marginBottom < 0)
			marginBottom = 0;
		ImageTextButton[] buttons = { mcButton1, mcButton2, mcButton3,
				mcButton4, this.submitButton, this.collectButton };
		for (ImageTextButton button : buttons) {
			this.adjustLayoutHeight(button, height);
			this.adjustLayoutMargins(button, horizontalMargin, marginTop,
					horizontalMargin, marginBottom);
			this.adjustLayoutMargins(button.button, shadowOffset, shadowOffset,
					shadowOffset, shadowOffset);
			this.adjustLayoutMargins(button.imageView, 2 * shadowOffset,
					2 * shadowOffset, 0, 0);
			button.button
					.setPadding(horizontalPadding, 0, horizontalPadding, 0);
			this.setButtonStyle(button,
					myPoll.imageUrlSetForDisplay.buttonImageL, screenConfig, myPoll.imageUrlSetForDisplay.buttonImageLSource);
		}

		// open answer edit text
		verticalMargin = screenConfig.heightWithPercentage(2.5325);
		this.adjustLayoutMargins(this.responseEditText, shadowOffset, 0,
				shadowOffset, verticalMargin);

		verticalMargin = screenConfig.heightWithPercentage(2.5325);
		this.adjustLayoutMargins(this.userInteractionLayout, 0, 0, 0,
				verticalMargin);

        //image poll
        if(myPoll.getType().equals("I")) {
            setImagePollLandscapeLayout(screenConfig);
        }


	}

	void setBorderImageWithUrl(String borderImageUrl, String source) {
        RequestCreator request;
        if (source.equals("NETWORK")) {
            request = Picasso.with(this).load(borderImageUrl);
        }
        else {
            final String cacheFilename = PolljoyCore.createFilenameFromUrl(this, borderImageUrl, "png");
            File cacheFile = new File(cacheFilename);
            request = Picasso.with(this).load(cacheFile);
        }
		int lengthLimit = Polljoy.BORDER_IMAGE_MAX_LENGTH;
		if (lengthLimit > 0) {
			request = request.resize(lengthLimit, lengthLimit).centerInside();
		}
		request.into(new Target() {

			@Override
			public void onBitmapFailed(Drawable arg0) {
			}

            @Override
            public void onPrepareLoad(Drawable arg0) {
            }

			@Override
			public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
				borderImageDrawable = new BitmapDrawable(getResources(), bitmap);
				final int alpha = myPoll.app.backgroundAlpha * 255 / 100;
				borderImageDrawable.setAlpha(alpha);
				borderImageOverlay.setImageDrawable(borderImageDrawable);
			}

		});
	}

	void setButtonStyle(final ImageTextButton button, String buttonImageUrl,
			PJScreenConfiguration screenConfig, String source) {
		button.setBackgroundColor(Color.TRANSPARENT);
		button.setTextColor(myPoll.app.getButtonFontColor());
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, screenConfig.fontSize);
		button.setMaxLines(2);
		GradientDrawable background = (GradientDrawable) getResources()
				.getDrawable(R.drawable.poll_view_button);
		GradientDrawable shadowDrawable = (GradientDrawable) getResources()
				.getDrawable(R.drawable.poll_view_button_shadow);
		background.setColor(myPoll.getButtonColor());
		background.setCornerRadius(myPoll.app.backgroundCornerRadius);
		setDrawableForView(button.button, background);

		shadowDrawable.setCornerRadius(myPoll.app.backgroundCornerRadius);
		shadowDrawable.setAlpha(75);

		if (myPoll.app.buttonShadow) {
			button.imageView.setImageDrawable(shadowDrawable);
		} else {
			button.imageView.setImageDrawable(null);
		}
        String cacheFilename = PolljoyCore.createFilenameFromUrl(this, buttonImageUrl, "png");
        File cacheFile = new File(cacheFilename);
        RequestCreator request;

        if (source.equals("NETWORK")) {
            request = Picasso.with(this).load(buttonImageUrl);
        }
        else {
            request = Picasso.with(this).load(cacheFile);
        }

        request.into(new Target() {
            @Override
            public void onBitmapFailed(Drawable arg0) {
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                setDrawableForView(button.button, null);
                button.imageView.setImageBitmap(bitmap);
                MarginLayoutParams parameters = (MarginLayoutParams) button.button
                        .getLayoutParams();
                button.imageView.setLayoutParams(parameters);
            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
            }
        });
	}

    void setImagePoll() {
        ImageTextButton[] imagePollImages = {this.imagePoll1, this.imagePoll2, this.imagePoll3, this.imagePoll4};
        if (myPoll.choiceImageUrl.size()>0) {
            this.imagePollMain.setText(myPoll.choices[0]);
            RequestCreator request;
            String choiceUrl = myPoll.choiceImageUrl.get(myPoll.choices[0]);
            if (myPoll.choiceImageUrlSource.get(choiceUrl).equals("NETWORK")) {
                request = Picasso.with(this).load(choiceUrl);
            }
            else {
                String cacheFilename = PolljoyCore.createFilenameFromUrl(this, choiceUrl, "png");
                File cacheFile = new File(cacheFilename);
                request =  Picasso.with(this).load(cacheFile);
            }

            request.into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                    imagePollMain.imageView.setImageBitmap(bitmap);
                }
                @Override
                public void onBitmapFailed(Drawable arg0) {
                }

                @Override
                public void onPrepareLoad(Drawable arg0) {
                }
            });

            int index=0;
            for(final ImageTextButton currentImage: imagePollImages) {
                if (index<myPoll.choiceImageUrl.size()) {
                    RequestCreator request1;
                    String choiceUrl1 = myPoll.choiceImageUrl.get(myPoll.choices[index]);
                    if (myPoll.choiceImageUrlSource.get(choiceUrl1).equals("NETWORK")) {
                        request1 = Picasso.with(this).load(choiceUrl1);
                    }
                    else {
                        String cacheFilename1 = PolljoyCore.createFilenameFromUrl(this, choiceUrl1, "png");
                        File cacheFile1 = new File(cacheFilename1);
                        request1 =  Picasso.with(this).load(cacheFile1);
                    }
                    request1.into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                            currentImage.imageView.setImageBitmap(bitmap);
                        }
                        @Override
                        public void onBitmapFailed(Drawable arg0) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable arg0) {
                        }

                    });
                    currentImage.setText(myPoll.choices[index]);
                    currentImage.setVisibility(View.VISIBLE);
                    currentImage.setTextSize(0);
                } else {
                    currentImage.imageView.setImageDrawable(null);
                    currentImage.setVisibility(View.INVISIBLE);
                }
                index++;
            }

        }
        else {
            this.imagePollMain.imageView.setImageDrawable(null);
            for(ImageTextButton currentImage: imagePollImages) {
                currentImage.imageView.setImageDrawable(null);
            }
        }
    }

	void setPollImage() {
		if (myPoll.imageUrlSetForDisplay.pollImageUrl != null) {
            if (myPoll.imageUrlSetForDisplay.pollImageUrlSource.equals("NETWORK")) {
                Picasso.with(this).load(myPoll.imageUrlSetForDisplay.pollImageUrl)
                        .into(this.pollImageView);
            }
            else {
                String cacheFilename = PolljoyCore.createFilenameFromUrl(this, myPoll.imageUrlSetForDisplay.pollImageUrl, "png");
                File cacheFile = new File(cacheFilename);
                Picasso.with(this).load(cacheFile)
                        .into(this.pollImageView);
            }
		} else {
			this.pollImageView.setImageDrawable(null);
		}
	}

	void setRewardImage() {
		String imageUrl = myPoll.imageUrlSetForDisplay.rewardImageUrl;
		if (imageUrl != null) {
            if (myPoll.imageUrlSetForDisplay.rewardImageUrlSource.equals("NETWORK")){
                Picasso.with(this).load(imageUrl).into(this.virtualAmountImageView);
                Picasso.with(this).load(imageUrl).into(this.rewardImageView);
            }
            else {
                String cacheFilename = PolljoyCore.createFilenameFromUrl(this, imageUrl, "png");
                File cacheFile = new File(cacheFilename);
                Picasso.with(this).load(cacheFile).into(this.virtualAmountImageView);
                Picasso.with(this).load(cacheFile).into(this.rewardImageView);
            }
		}
	}

	void setCloseButtonImage() {
		String imageUrl = myPoll.imageUrlSetForDisplay.closeButtonImageUrl;
		if (imageUrl != null) {
            RequestCreator request;
            if (myPoll.imageUrlSetForDisplay.closeButtonImageUrlSource.equals("NETWORK")) {
                request = Picasso.with(this).load(imageUrl);
            }
            else {
                String cacheFilename = PolljoyCore.createFilenameFromUrl(this, imageUrl, "png");
                File cacheFile = new File(cacheFilename);
                request = Picasso.with(this).load(cacheFile);
            }
            request.into(new Target() {
                @Override
                public void onBitmapFailed(Drawable arg0) {
                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                    closeButton.setColorFilter(null);
                    closeButton.setImageBitmap(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable arg0) {
                }
            });
		}
	}

	void userResponded(ImageTextButton button) {
		if (button == this.submitButton) {
			try {
				String input = responseEditText.getText().toString();
				String trimmedInput = input.replace(" ", "");
				if (trimmedInput.length() < 1) {
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.showSoftInput(responseEditText, 0);
					return;
				} else {
				}
                myPoll.response = input;
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			myPoll.response = button.getText().toString();
		}
		userResponded = true;
		
		if (Polljoy._rewardThankyouMessageStyle == PJRewardThankyouMessageStyle.PJRewardThankyouMessageStylePopup) {
			closeButton.setVisibility(View.VISIBLE);
			mcButtonsLayout.setVisibility(View.INVISIBLE);
			textResponseLayout.setVisibility(View.INVISIBLE);
	        imagePollLayout.setVisibility(View.INVISIBLE);
		}
		else {
			closeButton.setVisibility(View.VISIBLE);
		}
        answeredButton = button;
		this.delegate.PJPollViewDidAnswered(this, myPoll);
	}


	void userSkipped(View view) {
        playTapSound();
		if (userResponded) {
			userConfirmed(null);
		} else {
			this.delegate.PJPollViewDidSkipped(this, myPoll);
		}
	}

	void userConfirmed(View button) {
		this.delegate.PJPollViewCloseAfterResponse(this, myPoll);
		return;
	}

    void changeImagePoll(ImageTextButton view) {
        if (this.imagePollMain.button.getText().equals(view.getText())) {
            this.imagePollConfirm.setVisibility(View.VISIBLE);
        } else {
            this.imagePollConfirm.setVisibility(View.INVISIBLE);
        }
        RequestCreator request;
        String choiceUrl = myPoll.choiceImageUrl.get(view.getText().toString());
        if (myPoll.choiceImageUrlSource.get(choiceUrl).equals("NETWORK")) {
            request = Picasso.with(this).load(choiceUrl);
        }
        else {
            String cacheFilename = PolljoyCore.createFilenameFromUrl(this, choiceUrl, "png");
            File cacheFile = new File(cacheFilename);
            request =  Picasso.with(this).load(cacheFile);
        }

        request.into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, LoadedFrom arg1) {
                imagePollMain.imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onBitmapFailed(Drawable arg0) {
            }

            @Override
            public void onPrepareLoad(Drawable arg0) {
            }

        });
        this.imagePollMain.setText(view.getText().toString());

        playTapSound();
    }
    void clickImagePoll(ImageTextButton view) {
        this.imagePollConfirm.setVisibility(View.VISIBLE);

        playTapSound();
    }

    void setImagePollPortraitLayout (PJScreenConfiguration screenConfig) {
        double verticalMargin = screenConfig.getInnerHeight() - screenConfig.heightWithPercentage(35) - screenConfig.widthWithPercentage(80);
        verticalMargin /=2;
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) this.userInteractionLayout.getLayoutParams();
        p.weight=76.87f;
        this.userInteractionLayout.setLayoutParams(p);
        this.pollImageLayout.setVisibility(View.GONE);
        adjustLayoutMargins(this.offerLayout,screenConfig.widthWithPercentage(40),screenConfig.heightWithPercentage(24),0,0);
        int length = screenConfig.widthWithPercentage(60);
        this.adjustLayoutMargins(this.imagePollMain, 0, 0, 0, 0);
        this.adjustLayoutWidth(this.imagePollMain, length);
        this.adjustLayoutHeight(this.imagePollMain, length);
        this.adjustLayoutHeight(this.imagePollMain.button, 0);
        this.adjustLayoutWidth(this.imagePollMain.button, 0);
        this.adjustLayoutMargins(this.imagePollMain.imageView, 0, 0, 0, 0);
        this.adjustLayoutWidth(this.imagePollMain.imageView, length);
        this.adjustLayoutHeight(this.imagePollMain.imageView, length);

        this.adjustLayoutMargins(findViewById(R.id.imagePollMainLayout), 0, screenConfig.heightWithPercentage(8.25), 0, (int)verticalMargin);
        this.adjustLayoutMargins(findViewById(R.id.imagePollOtherLayout), 0, 0, 0,0);
        length = screenConfig.widthWithPercentage(20);
        ImageTextButton[] imagePollImages = {this.imagePoll1, this.imagePoll2, this.imagePoll3, this.imagePoll4};
        int index = 0;
        double imagePollImagesMargin = (screenConfig.widthWithPercentage(100)
                -(myPoll.choiceImageUrl.size()*length)
                -(2*screenConfig.widthWithPercentage(1.6667)) );
        imagePollImagesMargin /= (myPoll.choiceImageUrl.size()+1);
        for (ImageTextButton currentImage: imagePollImages) {
            if (index != 0) {
                this.adjustLayoutMargins(currentImage,(int)imagePollImagesMargin,0,0,0);
            }else {
                this.adjustLayoutMargins(currentImage,(int)imagePollImagesMargin+screenConfig.widthWithPercentage(1.6667),0,0,0);
            }
            this.adjustLayoutWidth(currentImage,length);
            this.adjustLayoutHeight(currentImage,length);
            this.adjustLayoutMargins(currentImage.button,0,0,0,0);
            this.adjustLayoutWidth(currentImage.button,0);
            this.adjustLayoutHeight(currentImage.button,0);
            this.adjustLayoutMargins(currentImage.imageView,0,0,0,0);
            this.adjustLayoutWidth(currentImage.imageView,length);
            this.adjustLayoutHeight(currentImage.imageView,length);
            index++;
        }
        this.imagePollConfirm.setBackgroundColor(Color.TRANSPARENT);
        this.imagePollConfirm.setTextColor(myPoll.app.getButtonFontColor());
        GradientDrawable background = (GradientDrawable) getResources()
                .getDrawable(R.drawable.poll_view_button);
        background.setColor(myPoll.getButtonColor());
        setDrawableForView(this.imagePollConfirm, background);
    }

    void setImagePollLandscapeLayout (PJScreenConfiguration screenConfig) {
        double horizontalMargin = screenConfig.getInnerWidth();
        this.adjustLayoutMargins(questionTextView, screenConfig.widthWithPercentage(27.125),0,screenConfig.widthWithPercentage(6),0);
        findViewById(R.id.rightSpacer).setVisibility(View.GONE);
        findViewById(R.id.leftSpacer).setVisibility(View.GONE);
        findViewById(R.id.innerSpacer).setVisibility(View.GONE);
        int length = screenConfig.heightWithPercentage(55);
        horizontalMargin -= (2*length);
        horizontalMargin /= 3;
        this.adjustLayoutMargins(this.imagePollMain, 0, 0, 0, 0);
        this.adjustLayoutWidth(this.imagePollMain, length);
        this.adjustLayoutHeight(this.imagePollMain, length);
        this.adjustLayoutHeight(this.imagePollMain.button, 0);
        this.adjustLayoutWidth(this.imagePollMain.button, 0);
        this.adjustLayoutMargins(this.imagePollMain.imageView, 0, 0, 0, 0);
        this.adjustLayoutWidth(this.imagePollMain.imageView, length);
        this.adjustLayoutHeight(this.imagePollMain.imageView, length);


/*        this.adjustLayoutMargins(findViewById(R.id.imagePollMainLayout), 0, screenConfig.heightWithPercentage(3),
                screenConfig.widthWithPercentage(3) , screenConfig.heightWithPercentage(3));*/
        this.adjustLayoutMargins(findViewById(R.id.imagePollMainLayout), 0, screenConfig.heightWithPercentage(3),
                (int)horizontalMargin , 0);

        length = screenConfig.heightWithPercentage(26);
        ImageTextButton[] imagePollImages = {this.imagePoll1, this.imagePoll2, this.imagePoll3, this.imagePoll4};
        int imagePollTopMargin, imagePollRightMargin;
        for (int i=0; i<imagePollImages.length; i++) {
            ImageTextButton currentImage = imagePollImages[i];
            if (i < 2 ) {
                imagePollRightMargin = screenConfig.heightWithPercentage(3);
            } else {
                imagePollRightMargin = 0;
            }
            if (i%2 == 0) {
                imagePollTopMargin = 0;
            } else {
                imagePollTopMargin = screenConfig.heightWithPercentage(3);
            }
            this.adjustLayoutMargins(currentImage, 0, imagePollTopMargin, imagePollRightMargin, 0);

            this.adjustLayoutWidth(currentImage, length);
            this.adjustLayoutHeight(currentImage, length);
            this.adjustLayoutMargins(currentImage.button, 0, 0, 0, 0);
            this.adjustLayoutWidth(currentImage.button,0);
            this.adjustLayoutHeight(currentImage.button, 0);
            this.adjustLayoutMargins(currentImage.imageView, 0, 0, 0, 0);
            this.adjustLayoutWidth(currentImage.imageView, length);
            this.adjustLayoutHeight(currentImage.imageView, length);
        }
//        adjustLayoutMargins(findViewById(R.id.imagePollOtherLayout),screenConfig.widthWithPercentage(3), screenConfig.heightWithPercentage(3),0,0);
        adjustLayoutMargins(findViewById(R.id.imagePollOtherLayout), 0,
                screenConfig.heightWithPercentage(3),0,0);

        this.imagePollConfirm.setBackgroundColor(Color.TRANSPARENT);
        this.imagePollConfirm.setTextColor(myPoll.app.getButtonFontColor());
        GradientDrawable background = (GradientDrawable) getResources()
                .getDrawable(R.drawable.poll_view_button);
        background.setColor(myPoll.getButtonColor());
        setDrawableForView(this.imagePollConfirm, background);
        this.pollImageLayout.setVisibility(View.GONE);
        adjustLayoutMargins(this.offerLayout,screenConfig.widthWithPercentage(6),screenConfig.heightWithPercentage(10),0,0);
    }

	public void showActionAfterResponse() {
		this.updatePollViewState(userResponded);
	}
	
	void showCollectMsg() {
		Rect rect = new Rect();
		answeredButton.getDrawingRect(rect);
		
		if (myPoll.type.equals("I")) {
			imagePollConfirm.setVisibility(View.INVISIBLE);
			answeredButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, answeredButton.getTextSize());
			answeredButton.message.setLines(2);

			this.adjustLayoutSize(answeredButton.messageImage, rect.height()/4, rect.height()/4);
		}
		else {
			this.adjustLayoutSize(answeredButton.messageImage, rect.height(), rect.height());
		}
		
		answeredButton.setTextColor(myPoll.app.getFontColor());
		answeredButton.setMessageText(String.valueOf(myPoll.virtualAmount) + " " +  myPoll.collectMsgText);
		answeredButton.messageImage.setImageBitmap(((BitmapDrawable)rewardImageView.getDrawable()).getBitmap());
		answeredButton.button.setVisibility(View.INVISIBLE);
		answeredButton.imageView.setVisibility(View.INVISIBLE);
		answeredButton.message.setVisibility(View.VISIBLE);
		answeredButton.messageImage.setVisibility(View.VISIBLE);
		answeredButton.setVisibility(View.VISIBLE);
		
		this.offerLayout.setVisibility(View.INVISIBLE);
		this.closeButton.setVisibility(View.INVISIBLE);
		this.enable(false);

		Handler delayHandler = new Handler();
		delayHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				userConfirmed(answeredButton);
			}
		}, (long) (Polljoy._messageShowDuration * 1000));
		
		
		playCollectSound();
	}
	
	//@SuppressWarnings("null")
	void showThankyouMsg() {

		if (myPoll.type.equals("I")) {
			imagePollConfirm.setVisibility(View.INVISIBLE);
			answeredButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, answeredButton.getTextSize());
		}
		
		answeredButton.setTextColor(myPoll.app.getFontColor());
		answeredButton.setMessageText(myPoll.thankyouMsgText);
		answeredButton.button.setVisibility(View.INVISIBLE);
		answeredButton.imageView.setVisibility(View.INVISIBLE);
		answeredButton.message.setVisibility(View.VISIBLE);
		answeredButton.messageImage.setVisibility(View.INVISIBLE);
		this.adjustLayoutSize(answeredButton.messageImage, 0, 0);
		answeredButton.setVisibility(View.VISIBLE);
		
		this.closeButton.setVisibility(View.INVISIBLE);
		this.offerLayout.setVisibility(View.INVISIBLE);
		this.enable(false);
		
		Handler delayHandler = new Handler();
		delayHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				userConfirmed(answeredButton);
			}
		}, (long) (Polljoy._messageShowDuration * 1000));

        playTapSound();
	}
	
	void playCollectSound() {
        if (Polljoy.customSound != null) {
            if (myPoll.app.customSoundUrl != null && !myPoll.app.customSoundUrl.equals("null") && myPoll.app.customSoundUrl.length() > 0) {
                Polljoy.customSound.start();
            }
        }
	}

    void playTapSound() {
        if (Polljoy.customTapSound != null) {
            if (myPoll.app.customTapSoundUrl != null && !myPoll.app.customTapSoundUrl.equals("null") && myPoll.app.customTapSoundUrl.length() > 0) {
                Polljoy.customTapSound.start();
            }
        }
    }

	public void enable(boolean b) {
	    enabled = b;
	}
}
