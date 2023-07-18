package com.vti.threadsmeta.util;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class I18n {

    private I18n(){}

    /**
     * @see com.vti.threadsmeta.config.ResourceBundleConfig
     */
    private static MessageSource messageSource;

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static void init(MessageSource inject) {
        messageSource = inject;
    }

    /**
     * Get Message by key
     *
     * @param key message key
     * @return Message contents
     */
    public static String get(String key) {
        return get(key, DEFAULT_LOCALE);
    }

    /**
     * Get Message by key then format with parameter values
     *
     * @param key    message key
     * @param params parameter values
     * @return formatted message
     */
    public static String get(String key, Object... params) {
        return messageSource.getMessage(key, params, DEFAULT_LOCALE);
    }

    /**
     * Get Message by message key and locale setting
     *
     * @param key    message key
     * @param locale locale
     * @return message contents by locale;
     */
    public static String get(String key, Locale locale, Object... params) {
        return messageSource.getMessage(key, params, locale);
    }
}
