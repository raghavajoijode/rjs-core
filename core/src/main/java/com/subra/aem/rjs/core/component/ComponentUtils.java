package com.subra.aem.rjs.core.component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

public class ComponentUtils {

    public static final String ID_SEPARATOR = "-";

    private ComponentUtils() {
        throw new IllegalArgumentException();
    }

    public static String getURL(SlingHttpServletRequest request, PageManager pageManager, String path) {
        Page page = pageManager.getPage(path);
        if (page != null) {
            return getURL(request, page);
        }
        return path;
    }

    @NotNull
    public static String getURL(@NotNull SlingHttpServletRequest request, @NotNull Page page) {
        String vanityURL = page.getVanityUrl();
        return StringUtils.isEmpty(vanityURL) ? (request.getContextPath() + page.getPath() + ".html") : (request.getContextPath() + vanityURL);
    }

    public static String generateId(String prefix, String path) {
        return StringUtils.join(prefix, ID_SEPARATOR, StringUtils.substring(DigestUtils.sha256Hex(path), 0, 10));
    }

    public enum HeadingTypes {

        H1("h1"), H2("h2"), H3("h3"), H4("h4"), H5("h5"), H6("h6");

        private final String element;

        HeadingTypes(String element) {
            this.element = element;
        }

        public static HeadingTypes getHeading(String value) {
            for (HeadingTypes heading : values()) {
                if (StringUtils.equalsIgnoreCase(heading.element, value)) {
                    return heading;
                }
            }
            return null;
        }

        public String getElement() {
            return element;
        }
    }

}
