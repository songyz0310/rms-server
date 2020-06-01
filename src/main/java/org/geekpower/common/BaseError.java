package org.geekpower.common;

public enum BaseError {
	UNKNOWN_ERROR(30001001),//
	ACCOUNT_NOT_EXIT(30001001),//
	;

	private final int code;

	private BaseError(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return this.name();
	}

}
