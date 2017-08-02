package com.os.app.beans;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CSVGenearteInputRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Size(min = 1)
	@NotNull
	private List<String> columns;

	@Size(min = 1)
	@NotNull
	private List<List<String>> data;

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CSVGenearteInputRequest [columns=" + columns + ", data.length=" + getData().size() + "]";
	}

}
