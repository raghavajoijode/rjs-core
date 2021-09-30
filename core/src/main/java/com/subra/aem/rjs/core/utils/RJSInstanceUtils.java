package com.subra.aem.rjs.core.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.WCMMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.cm.ConfigurationException;

import java.util.HashSet;
import java.util.Set;

public final class RJSInstanceUtils {

    private static boolean isAuthor = false;

    private static boolean isPublish = false;

    private static Set<String> runModes = new HashSet<>();

    private RJSInstanceUtils() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static void configure(SlingSettingsService slingSettings) throws ConfigurationException {
        runModes = slingSettings.getRunModes();
        isAuthor = runModes.contains(Externalizer.AUTHOR);
        isPublish = runModes.contains(Externalizer.PUBLISH);
        if (isAuthor && isPublish) {
            throw new ConfigurationException(null,
                    "Either 'author' or 'publish' run modes may be specified, not both.");
        }
    }

    public static boolean isAuthor() {
        return isAuthor;
    }

    public static boolean isPublish() {
        return isPublish;
    }

    public static boolean isRunMode(String mode) {
        return runModes.contains(mode);
    }

    public static boolean isAnalytics(SlingHttpServletRequest request) {
        return WCMMode.ANALYTICS == getMode(request);
    }

    public static boolean isDesign(SlingHttpServletRequest request) {
        return WCMMode.DESIGN == getMode(request);
    }

    public static boolean isDisabled(SlingHttpServletRequest request) {
        return WCMMode.DISABLED == getMode(request);
    }

    public static boolean isEdit(SlingHttpServletRequest request) {
        return WCMMode.EDIT == getMode(request);
    }

    public static boolean isPreview(SlingHttpServletRequest request) {
        return WCMMode.PREVIEW == getMode(request);
    }

    public static boolean isReadOnly(SlingHttpServletRequest request) {
        return WCMMode.READ_ONLY == getMode(request);
    }

    public static WCMMode getMode(SlingHttpServletRequest req) {
        if (req.getAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME) == null) {
            return WCMMode.DISABLED;
        } else {
            String mode = String.valueOf(req.getAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME));
            try {
                return WCMMode.valueOf(mode);
            } catch (IllegalArgumentException ex) {
                return WCMMode.DISABLED;
            }
        }
    }

    public static boolean isClassic(SlingHttpServletRequest request) {
        return AuthoringUIMode.CLASSIC == AuthoringUIMode.fromRequest(request);
    }

    public static boolean isTouch(SlingHttpServletRequest request) {
        return AuthoringUIMode.TOUCH == AuthoringUIMode.fromRequest(request);
    }

}