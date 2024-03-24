package com.app.whse.data;

public class Result {

	private boolean success;
	private int code;
	private String message;
	private Object dataObject;
	
	
	public Result() {
		this.success =false;
	}
	public Result(boolean success,int code, String message, Object dataObject) {
		super();
		this.success = success;
		this.code = code;
		this.message = message;
		this.dataObject = dataObject;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getDataObject() {
		return dataObject;
	}
	public void setDataObject(Object dataObject) {
		this.dataObject = dataObject;
	}
	@Override
	public String toString() {
		return "Result [code=" + code + ", message=" + message + ", dataObject=" + dataObject + "]";
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
