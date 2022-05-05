package com.cobnet.common;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static Map.Entry<String, String[]> substringsBetween(String str, String open, String close) {

        return new KeyValuePair<>(str.replaceAll(String.format("\\%s.*?\\%s", open, close), ""), new Delegate<>(new ArrayList<>()).invoke(delegator -> {
                Matcher m = Pattern.compile(String.format("\\%s([^)]+)\\%s", open, close)).matcher(str);
                while(m.find()) {
                    delegator.add(m.group(1));
                }
                return delegator;
        }).toArray(String[]::new));
    }
}
