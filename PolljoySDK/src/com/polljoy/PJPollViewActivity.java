package com.polljoy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.polljoy.internal.ImageDownloader;
import com.polljoy.internal.PolljoyCore;

public class PJPollViewActivity extends Activity {
	public interface PJPollViewActivityDelegate {
		void PJPollViewDidAnswered(PJPollViewActivity pollView, PJPoll poll);

		void PJPollViewDidSkipped(PJPollViewActivity pollView, PJPoll poll);

		void PJPollViewCloseAfterReponse(PJPollViewActivity pollView,
				PJPoll poll);
	}

	private PJPoll myPoll;
	PJPollViewActivityDelegate delegate;
	View pollViewLayout;
	ImageView pollImageView;
	ImageView virtualAmountImageView;
	View contentView;
	TextView questionTextView;
	TextView virtualAmountTextView;
	TextView virtualAmountRewardTextView;
	EditText responseEditText;
	View mcButtonsLayout;
	Button mcButton1;
	Button mcButton2;
	Button mcButton3;
	Button mcButton4;
	View textResponseLayout;
	Button submitButton;
	Button collectButton;
	ImageButton closeButton;

	boolean userResponded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myPoll = (PJPoll) this.getIntent().getExtras().get("Poll");
		this.delegate = Polljoy.getPollViewActivityDelegate();
		setupViews();
		this.setPollImage();
		this.userResponded = false;
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

	void setupViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_poll_view);
		this.pollViewLayout = (View) findViewById(R.id.pollViewLayout);
		this.contentView = (View) findViewById(R.id.pollView);
		this.pollImageView = (ImageView) findViewById(R.id.pollImageView);
		this.virtualAmountImageView = (ImageView) findViewById(R.id.virtualAmountImageView);
		this.virtualAmountTextView = (TextView) findViewById(R.id.virtualAmountTextView);
		this.virtualAmountRewardTextView = (TextView) findViewById(R.id.virtualAmountRewardTextView);
		this.questionTextView = (TextView) findViewById(R.id.questionTextView);
		this.mcButtonsLayout = (View) findViewById(R.id.mcButtonsLayout);
		this.mcButton1 = (Button) findViewById(R.id.mcButton1);
		this.mcButton2 = (Button) findViewById(R.id.mcButton2);
		this.mcButton3 = (Button) findViewById(R.id.mcButton3);
		this.mcButton4 = (Button) findViewById(R.id.mcButton4);
		Button[] mcButtons = { mcButton1, mcButton2, mcButton3, mcButton4 };
		this.textResponseLayout = (View) findViewById(R.id.textResponseLayout);
		this.responseEditText = (EditText) findViewById(R.id.responseEditText);
		this.submitButton = (Button) findViewById(R.id.submitButton);
		this.collectButton = (Button) findViewById(R.id.collectButton);
		this.closeButton = (ImageButton) findViewById(R.id.closeButton);

		this.pollViewLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				pollViewLayout.setFocusable(true);
				pollViewLayout.setFocusableInTouchMode(true);
				pollViewLayout.requestFocus();
				return false;
			}

		});
		// Colors, Appearances

		GradientDrawable backGroundDrawable = (GradientDrawable) this
				.getResources().getDrawable(R.drawable.poll_view_border);
		backGroundDrawable.setStroke(4, myPoll.borderColor);
		backGroundDrawable.setColor(myPoll.backgroundColor);
		this.setDrawableForView(this.contentView, backGroundDrawable);

		this.questionTextView.setTextColor(myPoll.getFontColor());
		this.questionTextView.setText(myPoll.getPollText());

		OnClickListener submisionListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userResponded((Button) v);
			}

		};
		for (Button button : mcButtons) {
			button.setVisibility(View.INVISIBLE);
			button.setBackgroundColor(myPoll.getButtonColor());
			button.setTextColor(myPoll.getFontColor());
			button.setOnClickListener(submisionListener);
		}
		this.submitButton.setBackgroundColor(myPoll.getButtonColor());
		this.submitButton.setTextColor(myPoll.getFontColor());
		this.submitButton.setOnClickListener(submisionListener);
		this.collectButton.setBackgroundColor(myPoll.getButtonColor());
		this.collectButton.setTextColor(myPoll.getFontColor());
		OnClickListener collectListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userConfirmed((Button) v);
			}

		};
		this.collectButton.setOnClickListener(collectListener);
		OnClickListener closeButtonListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				userSkipped(v);
			}

		};
		PorterDuffColorFilter colorFilter = 
		        new PorterDuffColorFilter(myPoll.fontColor, PorterDuff.Mode.SRC_ATOP);
		this.closeButton.setColorFilter(colorFilter);
		this.closeButton.setOnClickListener(closeButtonListener);
		this.closeButton.setVisibility(myPoll.isMandatory() ? View.INVISIBLE
				: View.VISIBLE);
		this.closeButton.setColorFilter(colorFilter);
		this.virtualAmountImageView.setColorFilter(colorFilter);
		try {
			if (myPoll.type.equals("M")) {
				String[] choices = myPoll.choice.split(",");
				int offset = mcButtons.length - choices.length;

				for (int i = 0; i < choices.length; i++) {
					Button button = mcButtons[i + offset];
					button.setVisibility(View.VISIBLE);
					button.setText(choices[i]);
				}
				mcButtonsLayout.setVisibility(View.VISIBLE);
				textResponseLayout.setVisibility(View.INVISIBLE);
			} else if (myPoll.type.equals("T")) {
				mcButtonsLayout.setVisibility(View.INVISIBLE);
				textResponseLayout.setVisibility(View.VISIBLE);
			} else {
				mcButtonsLayout.setVisibility(View.INVISIBLE);
				textResponseLayout.setVisibility(View.INVISIBLE);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			mcButtonsLayout.setVisibility(View.INVISIBLE);
			textResponseLayout.setVisibility(View.INVISIBLE);
		}
		if (myPoll.virtualAmount > 0) {
			this.virtualAmountTextView.setText(String
					.valueOf(myPoll.virtualAmount));
			this.virtualAmountTextView.setVisibility(View.VISIBLE);
			this.virtualAmountTextView.setTextColor(myPoll.fontColor);
			this.virtualAmountRewardTextView.setVisibility(View.VISIBLE);
			this.virtualAmountRewardTextView.setTextColor(myPoll.fontColor);
		} else {
			this.virtualAmountTextView.setVisibility(View.INVISIBLE);
			this.virtualAmountRewardTextView.setVisibility(View.INVISIBLE);
		}

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
		this.contentView.setBackground(drawable);
	}

	@SuppressWarnings("deprecation")
	void setBackgroundDrawable(View view, Drawable drawable) {
		this.contentView.setBackgroundDrawable(drawable);
	}

	void setPollImage() {
		if (myPoll.imageUrlToDisplay != null) {
			ImageDownloader imageDownloader = Polljoy.getImageDownloader();
			imageDownloader.download(myPoll.imageUrlToDisplay,
					this.pollImageView, null);
		} else {
			Drawable drawable = PolljoyCore.defaultImage(this);
			this.pollImageView.setImageDrawable(drawable);
		}
	}

	void userResponded(Button button) {
		if (button == this.submitButton) {
			try {
				String input = responseEditText.getText().toString();
				String trimmedInput = input.replace(" ", "");
				if (trimmedInput.length() < 1) {
					// TODO: first responder
				} else {
					myPoll.response = input;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			myPoll.response = button.getText().toString();
		}
		userResponded = true;
		closeButton.setVisibility(View.VISIBLE);
		mcButtonsLayout.setVisibility(View.INVISIBLE);
		textResponseLayout.setVisibility(View.INVISIBLE);
		this.delegate.PJPollViewDidAnswered(this, myPoll);
	}

	void userSkipped(View view) {
		if (userResponded) {
			userConfirmed(null);
		} else {
			this.delegate.PJPollViewDidSkipped(this, myPoll);
		}
	}

	void userConfirmed(Button button) {
		this.delegate.PJPollViewCloseAfterReponse(this, myPoll);
		return;
	}

	public void showActionAfterResponse() {
		this.questionTextView.setText(myPoll.customMessage);
		if (myPoll.virtualAmount > 0) {
			collectButton.setVisibility(View.VISIBLE);
			closeButton.setVisibility(View.INVISIBLE);
		} else {
			collectButton.setVisibility(View.INVISIBLE);
			closeButton.setVisibility(View.VISIBLE);
		}
	}

}
