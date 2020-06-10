package com.news.newsaggregator.serializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Сериализатор поля pubDate сущности News в строковое значение.
 * @author Alexandr Trifonov
 *
 */
public class PubDateSerializer extends StdSerializer<ZonedDateTime> {
	public PubDateSerializer() {
		super(ZonedDateTime.class);
	}
	
	@Override
	public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.format(DateTimeFormatter.ofPattern("dd MMM yy HH:mm", Locale.getDefault())));
		
	}
}
