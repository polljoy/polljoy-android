package com.polljoy;

public enum PJResponseStatus {
	PJSuccess(0),
    PJSessionRegistrationFail(1),
    PJSessionMauLimitReached(2),
    PJNoPollFound(100),
    PNSessionQuotaReached(102),
    PJDailyQuotaReached(103),
    PJUserQuotaReached(104),
    PJInvalidRequest(110),
    PJInvalidResponse(301),
    PJUnknownError(302),
    PJAlreadyResponded(303),
    PJInvalidPollToken(310),
    PJUserAccountProblem(999),
    ;
	private final int statusCode;

	PJResponseStatus(int statusCode) {
		this.statusCode = statusCode;
	}

	int statusCode() {
		return statusCode;
	}
	static PJResponseStatus responseStatusForCode(int statusCode) {
		PJResponseStatus[] values = PJResponseStatus.values();
		for(int i =0 ; i<values.length; i++) {
			PJResponseStatus status = values[i];
			if(status.statusCode == statusCode) {
				return status;
			}
		}
		return null;
	}
}