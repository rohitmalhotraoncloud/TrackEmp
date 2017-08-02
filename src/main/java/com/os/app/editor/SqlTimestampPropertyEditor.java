package com.os.app.editor;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class SqlTimestampPropertyEditor extends PropertyEditorSupport {

	public static final String DEFAULT_BATCH_PATTERN = "yyyy-MM-dd";

	private final SimpleDateFormat sdf;

	/**
	 * uses default pattern yyyy-MM-dd for date parsing.
	 */
	public SqlTimestampPropertyEditor() {
		this.sdf = new SimpleDateFormat(SqlTimestampPropertyEditor.DEFAULT_BATCH_PATTERN);
	}

	/**
	 * Uses the given pattern for dateparsing, see {@link SimpleDateFormat} for
	 * allowed patterns.
	 * 
	 * @param pattern
	 *            the pattern describing the date and time format
	 * @see SimpleDateFormat#SimpleDateFormat(String)
	 */
	public SqlTimestampPropertyEditor(String pattern) {
		this.sdf = new SimpleDateFormat(pattern);
	}

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateAsText = sdf.format(new Date(Long.parseLong(text)));
			try {
				setValue(sdf.parse(dateAsText));
			} catch (ParseException e) {

			}
		} catch (Exception ex) {

		}
	}

}