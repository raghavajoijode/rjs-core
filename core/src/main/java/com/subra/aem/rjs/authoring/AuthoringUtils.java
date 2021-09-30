package com.subra.aem.rjs.authoring;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Optional;

public class AuthoringUtils {
    private static final String ITEM = "item";

    public static String getPagePathForDialogRequest(final SlingHttpServletRequest request) {
        return Optional.of(request).map(req -> req.getParameter(ITEM)).orElse(StringUtils.EMPTY);
    }

    public static String getGroupName(final String pagePath, final String groupSuffix) {
        final String market = getMarket(pagePath);
        return StringUtils.isNotBlank(market) ? groupSuffix + market : StringUtils.EMPTY;
    }

    public static String getGroupName(final SlingHttpServletRequest request, final String groupSuffix) {
        final String market = getMarket(getPagePathForDialogRequest(request));
        return StringUtils.isNotBlank(market) ? groupSuffix + market : StringUtils.EMPTY;
    }

    private static String getMarket(final String pagePath) {
        String[] pageStructure = StringUtils.split(pagePath, "/");
        return pageStructure.length > 1 ? pageStructure[2] : StringUtils.EMPTY;
    }


}
