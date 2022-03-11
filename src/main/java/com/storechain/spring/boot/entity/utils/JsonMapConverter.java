package com.storechain.spring.boot.entity.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<HashMap<String,Object>, String> {

	private final ObjectMapper mapper;
	
	public JsonMapConverter() {
		
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new JavaTimeModule());
	}
	
	@Override
	public String convertToDatabaseColumn(HashMap<String, Object> meta) {
		try {
			return mapper.writeValueAsString(meta);

		} catch (JsonProcessingException ex) {

			return null;
		}
	}

	@Override
	public HashMap<String, Object> convertToEntityAttribute(String dbData) {
		try {
			
			return dbData != null ? mapper.readValue(dbData, HashMap.class) : new HashMap<String,Object>();

		} catch (IOException ex) {

			return null;
		}
	}


}
