package com.os.app.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Component
public class CustomDateDeserializer extends JsonSerializer<Timestamp> {
	@Override
	public void serialize(Timestamp value, JsonGenerator gen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {

		Date d = new Date(value.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = formatter.format(d);
		gen.writeString(formattedDate);

	}
}
