package com.polljoy;

public enum PJCloseButtonLocation {
	TopLeft(0),
	TopRight(1);
	private final int locationCode;

	PJCloseButtonLocation(int locationCode) {
		this.locationCode = locationCode;
	}

	int locationCode() {
		return locationCode;
	}
	
	static PJCloseButtonLocation locationForCode(int code) {
		PJCloseButtonLocation[] values = PJCloseButtonLocation.values();
		for(int i =0 ; i<values.length; i++) {
			PJCloseButtonLocation location = values[i];
			if(location.locationCode == code) {
				return location;
			}
		}
		return null;
	}
}
