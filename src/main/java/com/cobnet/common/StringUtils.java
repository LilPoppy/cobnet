package com.cobnet.common;

import java.util.Map;
import java.util.regex.Pattern;

public class StringUtils {

    public static Map.Entry<String, String> keyValuePair(String str, String open, String close) {

        return new KeyValuePair<>(str.replaceAll(open + ".*?" + close, ""), org.apache.commons.lang.StringUtils.substringBetween(str, open, close));
    }
}
