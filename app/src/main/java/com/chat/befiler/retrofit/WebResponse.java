package com.chat.befiler.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WebResponse<T> {

	@SerializedName("isSuccess")
	@Expose
	private boolean isSuccess;

	@SerializedName("message")
	@Expose
	private String message;

	@SerializedName("result")
	@Expose
	private T result;

	@SerializedName("id")
	@Expose
	private int id;


	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}