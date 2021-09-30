package com.subra.aem.rjs.core.rewriters.transformer.factory;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.subra.aem.rjs.core.rewriters.RewriterUtils;
import com.subra.aem.rjs.core.rewriters.transformer.AbstractTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.utils.RJSDateTimeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component(property = {"pipeline.type=rjs-versioned-clientlibs"}, service = {TransformerFactory.class})
public class VersionedClientLibTransformerFactory implements TransformerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionedClientLibTransformerFactory.class);
    private static final String PROXY_PREFIX = "/etc.clientlibs/";
    private static final String ATTR_JS_PATH = "src";
    private static final String ATTR_CSS_PATH = "href";
    private static final String MIN_SELECTOR = "min";
    private static final String MIN_SELECTOR_SEGMENT = "." + MIN_SELECTOR;
    @Reference
    private HtmlLibraryManager htmlLibraryManager;
    private Map<String, ClientLibrary> clientLibrariesCache;

    @Override
    public Transformer createTransformer() {
        LOGGER.trace("Triggering createTransformer {}", this.getClass().getName());
        return new VersionedClientLibTransformer();
    }

    public class VersionedClientLibTransformer extends AbstractTransformer {

        private SlingHttpServletRequest request;

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            super.init(context, config);
            this.request = context.getRequest();
        }

        @Override
        public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attributes) throws SAXException {
            final Attributes nextAttributes;
            nextAttributes = versionClientLibs(localName, attributes, request);
            getContentHandler().startElement(namespaceURI, localName, qName, nextAttributes);
        }

        private Attributes versionClientLibs(final String elementName, final Attributes attrs, final SlingHttpServletRequest request) {
            if (RewriterUtils.isCss(elementName, attrs)) {
                return this.rebuildAttributes(new AttributesImpl(attrs), attrs.getIndex("", ATTR_CSS_PATH), attrs.getValue("", ATTR_CSS_PATH), LibraryType.CSS, request);
            } else if (RewriterUtils.isJavaScript(elementName, attrs)) {
                return this.rebuildAttributes(new AttributesImpl(attrs), attrs.getIndex("", ATTR_JS_PATH), attrs.getValue("", ATTR_JS_PATH), LibraryType.JS, request);
            }
            return attrs;
        }

        private Attributes rebuildAttributes(final AttributesImpl newAttributes, final int index, final String path, final LibraryType libraryType, final SlingHttpServletRequest request) {
            final String contextPath = request.getContextPath();
            String libraryPath = path;
            if (StringUtils.isNotBlank(contextPath)) {
                libraryPath = path.substring(contextPath.length());
            }

            String versionedPath = this.getVersionedPath(libraryPath, libraryType, request.getResourceResolver());

            if (StringUtils.isNotBlank(versionedPath)) {
                if (StringUtils.isNotBlank(contextPath)) {
                    versionedPath = contextPath + versionedPath;
                }
                LOGGER.trace("Rewriting to: {}", versionedPath);
                newAttributes.setValue(index, versionedPath);
            } else {
                LOGGER.trace("Versioned Path could not be created properly");
            }

            return newAttributes;
        }

        private String getVersionedPath(final String originalPath, final LibraryType libraryType, final ResourceResolver resourceResolver) {
            try {
                boolean appendMinSelector = false;
                String libraryPath = StringUtils.substringBeforeLast(originalPath, ".");
                if (libraryPath.endsWith(MIN_SELECTOR_SEGMENT)) {
                    appendMinSelector = true;
                    libraryPath = StringUtils.substringBeforeLast(libraryPath, ".");
                }
                final HtmlLibrary htmlLibrary = getLibrary(libraryType, libraryPath, resourceResolver);
                if (htmlLibrary != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(libraryPath);
                    builder.append(".");

                    if (appendMinSelector) {
                        builder.append(MIN_SELECTOR).append(".");
                    }
                    builder.append(RJSDateTimeUtils.localDateTimeString("yyyyMMddHHmm"));
                    builder.append(libraryType.extension);

                    return builder.toString();
                } else {
                    LOGGER.trace("Could not find HtmlLibrary at path: {}", libraryPath);
                    return null;
                }
            } catch (Exception ex) {
                // Handle unexpected formats of the original path
                LOGGER.error("Attempting to get a versioned path for [ {} ] but could not because of: {}", originalPath,
                        ex.getMessage());
                return originalPath;
            }
        }

        private HtmlLibrary getLibrary(LibraryType libraryType, String libraryPath, ResourceResolver resourceResolver) {
            String resolvedLibraryPath = resolvePathIfProxified(libraryType, libraryPath, resourceResolver);
            return resolvedLibraryPath == null ? null : htmlLibraryManager.getLibrary(libraryType, resolvedLibraryPath);
        }

        private String resolvePathIfProxified(LibraryType libraryType, String libraryPath, ResourceResolver resourceResolver) {
            if (!libraryPath.startsWith(PROXY_PREFIX)) {
                return libraryPath;
            }
            return resolveProxiedClientLibrary(libraryType, libraryPath, resourceResolver, true);
        }

        private String resolveProxiedClientLibrary(LibraryType libraryType, String proxiedPath, ResourceResolver resourceResolver, boolean refreshCacheIfNotFound) {
            final String relativePath = proxiedPath.substring(PROXY_PREFIX.length());
            for (final String prefix : resourceResolver.getSearchPath()) {
                final String absolutePath = prefix + relativePath;
                // check whether the ClientLibrary exists before calling
                // HtmlLibraryManager#getLibrary in order
                // to avoid WARN log messages that are written when an unknown HtmlLibrary is
                // requested
                if (hasProxyClientLibrary(libraryType, absolutePath)) {
                    return absolutePath;
                }
            }

            if (refreshCacheIfNotFound) {
                // maybe the library has appeared and our copy of the cache is stale
                LOGGER.trace("Refreshing client libraries cache, because {} could not be found", proxiedPath);
                clientLibrariesCache = null;
                return resolveProxiedClientLibrary(libraryType, proxiedPath, resourceResolver, false);
            }
            return null;
        }

        private boolean hasProxyClientLibrary(final LibraryType type, final String path) {
            ClientLibrary clientLibrary = getClientLibrary(path);
            return clientLibrary != null && clientLibrary.allowProxy() && clientLibrary.getTypes().contains(type);
        }

        private ClientLibrary getClientLibrary(String path) {
            if (clientLibrariesCache == null) {
                clientLibrariesCache = Collections.unmodifiableMap(htmlLibraryManager.getLibraries());
            }
            return clientLibrariesCache.get(path);
        }

    }
}