package com.subra.aem.rjs.examples.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Comparator;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=ContactUsFormServlet Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/test/jsoup"})
@ServiceDescription("Simple JSoup Demo Servlet")
public class JSOUPSimpleServlet extends SlingSafeMethodsServlet {

    private static final String SUPERSCRIPTS = "sup.dis";
    private static final String SUPERSCRIPTS_WRAPPER = "sup-wrapper";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String text = request.getParameter("text");
        String test = "<p>Sample text line<sup data-val=\"1-a\" class=\"dis\">a</sup> 1 end</p> <p>Sample text line<sup data-val=\"2-b\" class=\"dis\">b</sup><sup data-val=\"2-2\" class=\"dis\">2</sup><sup data-val=\"2-d\" class=\"dis\">d</sup><sup  data-val=\"2-c\" class=\"dis\">c</sup> 2 end</p><p>Sample<sup  data-val=\"3-6\" class=\"dis\">6</sup> text line<sup  data-val=\"3-4\" class=\"dis\">4</sup><sup  data-val=\"3-2\" class=\"dis\">2</sup> 3 end</p><p>Sample<sup  data-val=\"4-6\" class=\"dis\">6</sup><sup  data-val=\"4-2\" class=\"dis\">2</sup> text line<sup  data-val=\"4-4\" class=\"dis\">4</sup><sup  data-val=\"4-2\" class=\"dis\">2</sup> 4 end</p><p>Sample<sup  data-val=\"5-6\" class=\"dis\">6</sup><sup  data-val=\"5-2\" class=\"dis\">2</sup> text line<sup  data-val=\"5-4\" class=\"dis\">4</sup><sup  data-val=\"5-2\" class=\"dis\">2</sup> 5 end<sup  data-val=\"5-l\" class=\"dis\">l</sup></p><p>Sample<sup  data-val=\"6-6\" class=\"dis\">6</sup> text line<sup  data-val=\"6-4\" class=\"dis\">4</sup><sup  data-val=\"6-2\" class=\"dis\">2</sup> 6 end<sup  data-val=\"6-l\" class=\"dis\">l</sup><sup  data-val=\"6-j\" class=\"dis\">j</sup></p>";
        Document document = Jsoup.parseBodyFragment(StringUtils.defaultIfBlank(text, test));
        String key = request.getParameter("key");
        if (!StringUtils.equalsIgnoreCase(key, "text")) {
            sortSuperScripts(document);
        }
        response.getWriter().write(document.toString());
    }

    private void sortSuperScripts(Document document) {
        document.select(SUPERSCRIPTS).forEach(el -> {
            if (hasNextAdjacentElement(el)) {
                el.html(sortedSiblingsMarkUp(el));
                for (Element nextElement : nextElementSiblings(true, el)) { // el.nextElementSiblings()
                    if (hasPrevAdjacentElement(nextElement))
                        nextElement.remove();
                    else
                        break;
                }
                el.addClass(SUPERSCRIPTS_WRAPPER);
                el.children().forEach(sortedElement -> {
                    if (sortedElement.nextSibling() instanceof TextNode || hasNextAdjacentElement(sortedElement))
                        sortedElement.append(",");
                });
            }
        });
        document.select("." + SUPERSCRIPTS_WRAPPER).forEach(Element::unwrap);
    }

    private String sortedSiblingsMarkUp(Element el) {
        Elements validSiblings = new Elements();
        validSiblings.add(el);
        for (Element e : nextElementSiblings(true, el)) {
            validSiblings.add(e);
            if (!hasNextAdjacentElement(e))
                break;
        }
        validSiblings.sort(Comparator.comparing(Element::ownText));
        return StringUtils.normalizeSpace(validSiblings.outerHtml());
    }

    private boolean hasNextAdjacentElement(Element element) {
        Node nextSibling = element.nextSibling();
        return nextSibling != null && nextSibling.attr("class").equalsIgnoreCase(element.className());
    }

    private boolean hasPrevAdjacentElement(Element element) {
        Node prevSibling = element.previousSibling();
        return prevSibling != null && prevSibling.attr("class").equalsIgnoreCase(element.className());
    }

    // Custom way of el.nextElementSiblings() el.previousElementSiblings()
    private Elements nextElementSiblings(boolean next, Element element) {
        Elements els = new Elements();
        if (element.parentNode() == null) {
            return els;
        } else {
            els.add(element);
            return next ? els.nextAll() : els.prevAll();
        }
    }

}
