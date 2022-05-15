package com.cobnet.common;

import jodd.util.StringUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static Map.Entry<String, String[]> substringsBetween(String str, char open, char close) {

        return new AbstractMap.SimpleEntry<>(str.replaceAll(String.format("\\%s.*?\\%s", open, close), ""), new Delegate<>(new ArrayList<>()).call(delegator -> {

            Matcher matcher = Pattern.compile(String.format("\\%s(.*?)\\%s", open, close)).matcher(str);

            while(matcher.find()) {

                delegator.add(matcher.group(1));
            }

        }).toArray(String[]::new));
    }

    public static String secure(String text, int start, int end, char replacement) {

        return new StringBuilder(text).replace(start, end, StringUtil.repeat(replacement, end - start)).toString();
    }
}
