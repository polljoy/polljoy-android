package com.polljoy;

public enum PJPollImageStatus {
	PJPollDefaultImageReady      (1 << 0),
	PJPollRewardImageReady       (1 << 1),
	PJPollCloseButtonImageReady  (1 << 2),
	PJPollBorderLImageReady      (1 << 3),
	PJPollBorderPImageReady      (1 << 4),
	PJPollButtonLImageReady      (1 << 5),
	PJPollButtonPImageReady      (1 << 6),
	PJPollAllImageReady          ((1 << 7) - 1);

	private final int statusCode;


	PJPollImageStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
