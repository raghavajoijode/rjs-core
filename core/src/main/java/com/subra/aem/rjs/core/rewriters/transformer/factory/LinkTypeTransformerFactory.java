package com.subra.aem.rjs.core.rewriters.transformer.factory;

import com.subra.aem.rjs.core.rewriters.transformer.AbstractTransformer;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

@Component(property = {"pipeline.type=rjs-linktype"}, service = {TransformerFactory.class})
public class LinkTypeTransformerFactory implements TransformerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkTypeTransformerFactory.class);

    private final Map<Pattern, String> iconMap = new HashMap<>();

    @Override
    public Transformer createTransformer() {
        LOGGER.trace("Triggering createTransformer {}", this.getClass().getName());
        return new LinkTypeTransformer();
    }

    public class LinkTypeTransformer extends AbstractTransformer {

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            LOGGER.trace("LinkTypeTransformer init...");
            for (String type : new String[]{"pdf", "doc", "xls"}) {
                iconMap.put(Pattern.compile(".+\\." + type + "($|#.*|\\?.*)", Pattern.CASE_INSENSITIVE), "/etc/designs/kajado/icons/" + type + ".png");
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (HTML.Tag.A.toString().equalsIgnoreCase(localName) && isDefinedType(attributes)) {
                getContentHandler().startElement(uri, localName, qName, attributes);
                addTypeIcon(attributes, getContentHandler());
            } else {
                getContentHandler().startElement(uri, localName, qName, attributes);
            }
        }

        private void addTypeIcon(Attributes elAttrs, ContentHandler contentHandler) throws SAXException {
            LOGGER.trace("addTypeIcon");

            String href = elAttrs.getValue(HTML.Attribute.HREF.toString());
            String icon = null;
            for (Entry<Pattern, String> entry : iconMap.entrySet()) {
                if (entry.getKey().matcher(href).matches()) {
                    icon = entry.getValue();
                    break;
                }
            }

            if (icon != null) {
                LOGGER.trace("Adding icon {} to link {}", icon, href);
                AttributesImpl atts = new AttributesImpl();
                atts.addAttribute("", HTML.Attribute.SRC.toString(), "", "", icon);
                contentHandler.startElement("", HTML.Tag.IMG.toString(), "", atts);
                contentHandler.endElement("", HTML.Tag.IMG.toString(), "");
            } else {
                LOGGER.warn("Unable to find icon for {}", href);
            }
        }

        private boolean isDefinedType(Attributes atts) {
            String href = atts.getValue(HTML.Attribute.HREF.toString());
            if (href != null) {
                for (Pattern pattern : iconMap.keySet()) {
                    if (pattern.matcher(href).matches()) {
                        LOGGER.trace("URL {} matches pattern {}", href, pattern);
                        return true;
                    }
                }
            }
            return false;
        }

    }

}