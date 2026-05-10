package com.estapar.gerenciadorestacionamento.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public class FlexibleInstantDeserializer extends JsonDeserializer<Instant> {

	@Override
	public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String value = parser.getValueAsString();
		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return Instant.parse(value);
		} catch (DateTimeParseException ignored) {
			return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
		}
	}
}
