package com.chat.befiler.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class WebResponse2<T> {

	@SerializedName("pageNumber")
	@Expose
	private int pageNumber;
	@SerializedName("pageSize")
	@Expose
	private int pageSize;
	@SerializedName("totalPages")
	@Expose
	private int totalPages;
	@SerializedName("totalRecords")
	@Expose
	private int totalRecords;

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

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
}