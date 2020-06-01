package org.geekpower.common;

public class RpcResponse<T> {

	private int ecode = 0;
	private String message = "OK";
	private T data;

	public RpcResponse(T data) {
		this.ecode = 0;
		this.message = "OK";
		this.data = data;
	}

	public RpcResponse(int ecode, String message) {
		this.ecode = ecode;
		this.message = message;
		this.data = null;
	}

	public int getEcode() {
		return ecode;
	}

	public void setEcode(int ecode) {
		this.ecode = ecode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
