package com.polljoy.internal;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.polljoy.R;

//public class ImageTextButton extends Button {
public class ImageTextButton extends RelativeLayout {
	public ImageView imageView;
	public Button button;

	public ImageTextButton(Context context) {
		super(context, null);
	}

	public ImageTextButton(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		LayoutInflater.from(context).inflate(R.layout.image_text_button, this,
				true);
		this.imageView = (ImageView) findViewById(R.id.imageView);
		this.button = (Button) findViewById(R.id.button);
		this.button.setTypeface(Typeface.SANS_SERIF);
		this.button.setClickable(false);
		this.button.setFocusable(false);
		this.button.setText(attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text"));
		this.setClickable(true);
		this.setFocusable(true);
	}

	public void setImageResource(int resourceID) {
		this.imageView.setImageResource(resourceID);
	}

	public void setText(String text) {
		this.button.setText(text);
	}

	public void setTextColor(int color) {
		this.button.setTextColor(color);
	}

	public void setTextSize(float size) {
		this.button.setTextSize(size);
	}

	public void setTextSize(int unit, float size) {
		this.button.setTextSize(unit, size);
	}

	public void setMaxLines(int maxlines) {
		this.button.setMaxLines(maxlines);
	}

	public void setBackgroundResource(int resid) {
		this.button.setBackgroundResource(resid);
	}

//	public void setOnTouchListener(OnTouchListener l) {
//		this.button.setOnTouchListener(l);
//	}

	public void setTypeface(Typeface tf) {
		this.button.setTypeface(tf);
	}

	public CharSequence getText() {
		// TODO Auto-generated method stub
		return this.button.getText();
	}

}
