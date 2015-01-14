package com.polljoy;

public enum PJRewardThankyouMessageStyle {
	PJRewardThankyouMessageStyleMessage(0),
    PJRewardThankyouMessageStylePopup(1)
    ;
	
	private final int value;

	private PJRewardThankyouMessageStyle(int value) {
		this.value = value;
	}

	int value() {
		return value;
	}
}