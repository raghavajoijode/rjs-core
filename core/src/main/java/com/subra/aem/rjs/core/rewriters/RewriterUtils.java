package com.subra.aem.rjs.core.rewriters;

import com.adobe.granite.ui.clientlibs.LibraryType;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;

public class RewriterUtils {

    public static final String CSS_TYPE = "text/css";
    public static final String JS_TYPE = "text/javascript";

    private RewriterUtils() {
    }

    public static boolean isCss(final String elementName, final Attributes attrs) {
        final String type = attrs.getValue("", "type");
        final String href = attrs.getValue("", "href");
        return StringUtils.equals("link", elementName) && StringUtils.equals(type, CSS_TYPE) && StringUtils.startsWith(href, "/") && !StringUtils.startsWith(href, "//") && StringUtils.endsWith(href, LibraryType.CSS.extension);
    }

    public static boolean isJavaScript(final String elementName, final Attributes attrs) {
        final String type = attrs.getValue("", "type");
        final String src = attrs.getValue("", "src");
        return StringUtils.equals("script", elementName) && StringUtils.equals(type, JS_TYPE) && StringUtils.startsWith(src, "/") && !StringUtils.startsWith(src, "//") && StringUtils.endsWith(src, LibraryType.JS.extension);
    }

}