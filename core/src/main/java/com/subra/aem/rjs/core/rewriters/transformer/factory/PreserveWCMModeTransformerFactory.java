package com.subra.aem.rjs.core.rewriters.transformer.factory;

import com.subra.aem.rjs.core.rewriters.transformer.AbstractTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.swing.text.html.HTML;
import java.io.IOException;

@Component(property = {"pipeline.type=rjs-preserve-wcmmode"}, service = {TransformerFactory.class})
public class PreserveWCMModeTransformerFactory implements TransformerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreserveWCMModeTransformerFactory.class);
    private static final String WCM_MODE_TEXT = "wcmmode";
    private static final String EQUALS_SYMBOL = "=";
    private static final String QUESTION_MARK_SYMBOL = "?";
    private static final String AMBER_SYMBOL = "&";

    @Override
    public Transformer createTransformer() {
        LOGGER.trace("Triggering createTransformer {}", this.getClass().getName());
        return new PreserveWCMModeTransformer();
    }

    public class PreserveWCMModeTransformer extends AbstractTransformer {
        private SlingHttpServletRequest request;

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            this.request = context.getRequest();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            String href = attributes.getValue(HTML.Attribute.HREF.toString());
            if (HTML.Tag.A.toString().equalsIgnoreCase(localName) && shouldBeTransformed()
                    && StringUtils.isNotBlank(href)) {
                String modifiedHref = href.isEmpty() ? href : modifyHref(href);
                AttributesImpl attributesImpl = new AttributesImpl(attributes);
                attributesImpl.setValue(attributes.getIndex("href"), modifiedHref);
                super.startElement(uri, localName, qName, attributesImpl);
            } else {
                super.startElement(uri, localName, qName, attributes);
            }
        }

        private boolean shouldBeTransformed() {
            return StringUtils.isNotBlank(request.getParameter(WCM_MODE_TEXT));
        }

        private String modifyHref(String href) {
            final String path = StringUtils.substringBefore(href, ".");
            final String afterExtension = StringUtils.substringAfterLast(href, ".html");
            StringBuilder modifyHref = new StringBuilder(href);
            if (request.getResourceResolver().getResource(path) != null) {
                if (StringUtils.isBlank(afterExtension))
                    modifyHref.append(QUESTION_MARK_SYMBOL + WCM_MODE_TEXT + EQUALS_SYMBOL).append(request.getParameter(WCM_MODE_TEXT));
                else if (afterExtension.contains("?") && !afterExtension.contains(WCM_MODE_TEXT + EQUALS_SYMBOL))
                    modifyHref.append(AMBER_SYMBOL + WCM_MODE_TEXT + EQUALS_SYMBOL).append(request.getParameter(WCM_MODE_TEXT));
            }
            return modifyHref.toString();
        }

    }

}