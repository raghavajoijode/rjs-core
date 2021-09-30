package com.subra.aem.rjs.core.utils;

import com.day.cq.i18n.I18n;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.osgi.service.cm.ConfigurationException;

import java.util.Locale;

public final class RJSI18NUtils {

    private static I18n i18n;

    private RJSI18NUtils() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static void configure(ResourceBundleProvider resourceBundleProvider) throws ConfigurationException {
        if (resourceBundleProvider == null) {
            throw new ConfigurationException(null, "resourceBundleProvider is null");
        }
        i18n = new I18n(resourceBundleProvider.getResourceBundle(Locale.ENGLISH));
    }

    public static String getI18nValue(final String text) {
        return i18n.get(text);
    }

    public static I18n getI18n() {
        return i18n;
    }

}