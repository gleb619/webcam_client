package org.test.data.types;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	private static final long serialVersionUID = -1527866021316284445L;
	
	private Boolean success;
	private Boolean warning;
	private String message;

	public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginResponse(Boolean success, Boolean warning, String message) {
		super();
		this.success = success;
		this.warning = warning;
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Boolean getWarning() {
		return warning;
	}

	public void setWarning(Boolean warning) {
		this.warning = warning;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
