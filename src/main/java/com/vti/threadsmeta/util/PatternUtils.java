package com.vti.threadsmeta.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

    public static String matcherPattern(String value, String regex) {
        Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.find() ? matcher.group(1) : "";
    }
}
