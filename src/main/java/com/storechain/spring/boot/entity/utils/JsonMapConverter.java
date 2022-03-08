package com.storechain.spring.boot.entity.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String,Object>, String> {

	private final static ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String,Object> meta) {
		try {

			return MAPPER.writeValueAsString(meta);

		} catch (JsonProcessingException ex) {

			return null;
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		try {
			
			return dbData != null ? MAPPER.readValue(dbData, Map.class) : new HashMap<String,Object>();

		} catch (IOException ex) {

			return null;
		}
	}


}
