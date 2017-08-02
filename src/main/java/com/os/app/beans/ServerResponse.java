package com.os.app.beans;

import java.io.Serializable;

public class ServerResponse implements Serializable {

	/**	
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	private boolean success;
	private Object data;

	public ServerResponse(Object data) {
		this.success = true;
		this.data = data;
	}

	public ServerResponse(String message, Object data) {
		this.message = message;
		this.success = true;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public Object getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

}
