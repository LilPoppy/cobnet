package com.cobnet.spring.boot.cache.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@WritingConverter
public class LocaleToBytesConverter implements Converter<Locale, byte[]> {


    @Override
    public byte[] convert(Locale source) {

        return new StringBuilder(source.getLanguage()).append("_").append(source.getCountry()).toString().getBytes();
    }
}
