package com.polljoy;

public enum PJUserType {
    PJPayUser("Pay"),
    PJNonPayUser("Non-Pay");
	private final String userTypeString;

	PJUserType(String userTypeString) {
		this.userTypeString = userTypeString;
	}

	String userTypeString() {
		return userTypeString;
	}
}
