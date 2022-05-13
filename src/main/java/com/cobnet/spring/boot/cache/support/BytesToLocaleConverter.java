package com.cobnet.spring.boot.cache.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Component
@ReadingConverter
public class BytesToLocaleConverter implements Converter<byte[], Locale> {

    @Override
    public Locale convert(byte[] source) {

        String[] codes = new String(source).split("_");

        if(codes.length > 0) {

            return new Locale(codes[0], codes[1]);
        }

        return null;
    }
}
