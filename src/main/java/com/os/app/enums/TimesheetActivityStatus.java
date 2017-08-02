package com.os.app.enums;

import java.util.HashMap;
import java.util.Map;

public enum TimesheetActivityStatus {

	SUBMITTED("submit"), APPROVED("approve"), REJECTED("reject"), TIMESHEETCLOSED("close");

	private static Map<String, TimesheetActivityStatus> content = new HashMap<>();

	private String status;

	static {
		for (TimesheetActivityStatus status : TimesheetActivityStatus.values()) {
			content.put(status.getStatus(), status);
		}
	}

	TimesheetActivityStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static TimesheetActivityStatus value(String status) {
		return content.get(status);
	}
}
