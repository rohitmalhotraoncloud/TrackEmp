package com.os.app.dto;

public class EmployeeCountDTO {
	Long total;
	Long active;
	Long inactive;

	public EmployeeCountDTO() {

	}

	public EmployeeCountDTO(Long total, Long active, Long inactive) {
		this.total = total;
		this.active = active;
		this.inactive = inactive;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}

	public Long getInactive() {
		return inactive;
	}

	public void setInactive(Long inactive) {
		this.inactive = inactive;
	}

}