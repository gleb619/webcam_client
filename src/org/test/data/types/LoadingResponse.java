package org.test.data.types;

import org.apache.http.HttpResponse;

public class LoadingResponse {

	private String result;
	private Integer code;
	private String log;
	private HttpResponse response;

	public LoadingResponse(String result, Integer code) {
		super();
		this.result = result;
		this.code = code;
	}

	public LoadingResponse(String result, Integer code, String log,
			HttpResponse response) {
		super();
		this.result = result;
		this.code = code;
		this.log = log;
		this.response = response;
	}

	public LoadingResponse(HttpResponse response) {
		super();
		this.response = response;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

}
