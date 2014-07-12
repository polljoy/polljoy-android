package com.polljoy;

import java.io.Serializable;

public class PJPollImageUrlSet implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8366180706014763258L;
	String rewardImageUrl = null;
	String closeButtonImageUrl = null;
	String pollImageUrl = null;
	String borderImageL = null;
	String borderImageP = null;
	String buttonImageL = null;
	String buttonImageP = null;
	int pollImageCornerRadius = 0;

	PJPollImageUrlSet(String rewardImageUrl, String closeButtonImageUrl,
			String pollImageUrl, String borderImageL, String borderImageP,
			String buttonImageL, String buttonImageP, int pollImageCornerRadius) {
		this.rewardImageUrl = rewardImageUrl;
		this.closeButtonImageUrl = closeButtonImageUrl;
		this.pollImageUrl = pollImageUrl;
		this.borderImageL = borderImageL;
		this.borderImageP = borderImageP;
		this.buttonImageL = buttonImageL;
		this.buttonImageP = buttonImageP;
		this.pollImageCornerRadius = pollImageCornerRadius;
	}

	PJPollImageUrlSet() {
	}

	public String getRewardImageUrl() {
		return rewardImageUrl;
	}

	public void setRewardImageUrl(String rewardImageUrl) {
		this.rewardImageUrl = rewardImageUrl;
	}

	public String getCloseButtonImageUrl() {
		return closeButtonImageUrl;
	}

	public void setCloseButtonImageUrl(String closeButtonImageUrl) {
		this.closeButtonImageUrl = closeButtonImageUrl;
	}

	public String getPollImageUrl() {
		return pollImageUrl;
	}

	public void setPollImageUrl(String pollImageUrl) {
		this.pollImageUrl = pollImageUrl;
	}

	public String getBorderImageL() {
		return borderImageL;
	}

	public void setBorderImageL(String borderImageL) {
		this.borderImageL = borderImageL;
	}

	public String getBorderImageP() {
		return borderImageP;
	}

	public void setBorderImageP(String borderImageP) {
		this.borderImageP = borderImageP;
	}

	public String getButtonImageL() {
		return buttonImageL;
	}

	public void setButtonImageL(String buttonImageL) {
		this.buttonImageL = buttonImageL;
	}

	public String getButtonImageP() {
		return buttonImageP;
	}

	public void setButtonImageP(String buttonImageP) {
		this.buttonImageP = buttonImageP;
	}

	public int getPollImageCornerRadius() {
		return pollImageCornerRadius;
	}

	public void setPollImageCornerRadius(int pollImageCornerRadius) {
		this.pollImageCornerRadius = pollImageCornerRadius;
	}
	
}
