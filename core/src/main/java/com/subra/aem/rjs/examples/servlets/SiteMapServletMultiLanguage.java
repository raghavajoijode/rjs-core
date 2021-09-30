package com.subra.aem.rjs.examples.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class, factory = "org.subra.aem.example.sample.servlets.CustomSiteMapServletMulti", configurationPolicy = ConfigurationPolicy.REQUIRE, property = {
		SLING_SERVLET_RESOURCE_TYPES + "=sling/servlet/default", SLING_SERVLET_EXTENSIONS + "=xml",
		SLING_SERVLET_METHODS + "=GET", SLING_SERVLET_SELECTORS + "=multilanguage-sitemap",
		"webconsole.configurationFactory.nameHint" + "="
				+ "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]" })
@Designate(ocd = SiteMapServletMultiLanguage.Config.class)
public class SiteMapServletMultiLanguage extends SlingSafeMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapServletMultiLanguage.class);
	private static final long serialVersionUID = 1L;

	private static final boolean DEFAULT_INCLUDE_LAST_MODIFIED = false;

	private static final boolean DEFAULT_INCLUDE_INHERITANCE_VALUE = false;

	private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";

	private static final boolean DEFAULT_EXTENSIONLESS_URLS = false;

	private static final boolean DEFAULT_REMOVE_TRAILING_SLASH = false;

	@ObjectClassDefinition(name = "ACS AEM Commons - Site Map Servlet", description = "Page and Asset Site Map Servlet")
	public @interface Config {

		@AttributeDefinition(name = "Sling Resource Type", description = "Sling Resource Type for the Home Page component or components.")
		String[] slingServletResourceType();

		@AttributeDefinition(defaultValue = DEFAULT_EXTERNALIZER_DOMAIN, name = "Externalizer Domain", description = "Must correspond to a configuration of the Externalizer component.")
		String externalizerDomain();

		@AttributeDefinition(defaultValue = ""
				+ DEFAULT_INCLUDE_LAST_MODIFIED, name = "Include Last Modified", description = "If true, the last modified value will be included in the sitemap.")
		boolean includeLastmod();

		@AttributeDefinition(name = "Change Frequency Properties", description = "The set of JCR property names which will contain the change frequency value.")
		String[] changefreqProperties();

		@AttributeDefinition(name = "Priority Properties", description = "The set of JCR property names which will contain the priority value.")
		String[] priorityProperties();

		@AttributeDefinition(name = "DAM Folder Property", description = "The JCR property name which will contain DAM folders to include in the sitemap.")
		String damassetsProperty();

		@AttributeDefinition(name = "DAM Asset MIME Types", description = "MIME types allowed for DAM assets.")
		String[] damassetsTypes();

		@AttributeDefinition(name = "Exclude from Sitemap Property", description = "The boolean [cq:Page]/jcr:content property name which indicates if the Page should be hidden from the Sitemap. Default value: hideInNav")
		boolean excludeProperty();

		@AttributeDefinition(defaultValue = ""
				+ DEFAULT_INCLUDE_INHERITANCE_VALUE, name = "Include Inherit Value", description = "If true searches for the frequency and priority attribute in the current page if null looks in the parent.")
		boolean includeInherit();

		@AttributeDefinition(defaultValue = ""
				+ DEFAULT_EXTENSIONLESS_URLS, name = "Extensionless URLs", description = "If true, page links included in sitemap are generated without .html extension and the path is included with a trailing slash, e.g. /content/geometrixx/en/.")
		boolean extensionlessUrls();

		@AttributeDefinition(defaultValue = ""
				+ DEFAULT_REMOVE_TRAILING_SLASH, name = "Remove Trailing Slash from Extensionless URLs", description = "Only relevant if Extensionless URLs is selected.  If true, the trailing slash is removed from extensionless page links, e.g. /content/geometrixx/en.")
		boolean removeSlash();

		@AttributeDefinition(name = "Character Encoding", description = "If not set, the container's default is used (ISO-8859-1 for Jetty)")
		String characterEncoding();

	}

	private static final String PROP_EXTERNALIZER_DOMAIN = "externalizer.domain";

	private static final String PROP_INCLUDE_LAST_MODIFIED = "include.lastmod";

	private static final String PROP_CHANGE_FREQUENCY_PROPERTIES = "changefreq.properties";

	private static final String PROP_PRIORITY_PROPERTIES = "priority.properties";

	private static final String PROP_EXCLUDE_FROM_SITEMAP_PROPERTY = "exclude.property";

	private static final String PROP_INCLUDE_INHERITANCE_VALUE = "include.inherit";

	private static final String PROP_EXTENSIONLESS_URLS = "extensionless.urls";

	private static final String PROP_REMOVE_TRAILING_SLASH = "remove.slash";

	private static final String PROP_CHARACTER_ENCODING_PROPERTY = "character.encoding";

	private static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

	private static final String NS_ALT = "http://www.w3.org/1999/xhtml";

	@Reference
	private transient Externalizer externalizer;

	private String externalizerDomain;

	private boolean includeInheritValue;

	private boolean includeLastModified;

	private String[] changefreqProperties;

	private String[] priorityProperties;

	private String excludeFromSiteMapProperty;

	private String characterEncoding;

	private boolean extensionlessUrls;

	private boolean removeTrailingSlash;

	@Activate
	protected void activate(Map<String, Object> properties) {
		this.externalizerDomain = PropertiesUtil.toString(properties.get(PROP_EXTERNALIZER_DOMAIN),
				DEFAULT_EXTERNALIZER_DOMAIN);
		this.includeLastModified = PropertiesUtil.toBoolean(properties.get(PROP_INCLUDE_LAST_MODIFIED),
				DEFAULT_INCLUDE_LAST_MODIFIED);
		this.includeInheritValue = PropertiesUtil.toBoolean(properties.get(PROP_INCLUDE_INHERITANCE_VALUE),
				DEFAULT_INCLUDE_INHERITANCE_VALUE);
		this.changefreqProperties = PropertiesUtil.toStringArray(properties.get(PROP_CHANGE_FREQUENCY_PROPERTIES),
				new String[0]);
		this.priorityProperties = PropertiesUtil.toStringArray(properties.get(PROP_PRIORITY_PROPERTIES), new String[0]);
		this.excludeFromSiteMapProperty = PropertiesUtil.toString(properties.get(PROP_EXCLUDE_FROM_SITEMAP_PROPERTY),
				NameConstants.PN_HIDE_IN_NAV);
		this.characterEncoding = PropertiesUtil.toString(properties.get(PROP_CHARACTER_ENCODING_PROPERTY), null);
		this.extensionlessUrls = PropertiesUtil.toBoolean(properties.get(PROP_EXTENSIONLESS_URLS),
				DEFAULT_EXTENSIONLESS_URLS);
		this.removeTrailingSlash = PropertiesUtil.toBoolean(properties.get(PROP_REMOVE_TRAILING_SLASH),
				DEFAULT_REMOVE_TRAILING_SLASH);
		LOGGER.debug("Unused Values are {}, {}, {}, {}, {}", excludeFromSiteMapProperty, extensionlessUrls,
				removeTrailingSlash, includeInheritValue, includeLastModified);
	}

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType(request.getResponseContentType());
		response.setCharacterEncoding(characterEncoding);
		ResourceResolver resourceResolver = request.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = pageManager.getContainingPage(request.getResource());

		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		try {
			XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
			stream.writeStartDocument("1.0");

			stream.writeStartElement("", "urlset", NS);
			stream.writeNamespace("", NS);
			stream.writeNamespace("xhtml", NS_ALT);
			page = page.getParent();
			for (Iterator<Page> children = page.listChildren(new PageFilter()); children.hasNext();) {

				write(children.next(), stream, resourceResolver);
			}
			LOGGER.debug("Unused Values are {}, {}, {}, {}, {}", excludeFromSiteMapProperty, extensionlessUrls,
					removeTrailingSlash, includeInheritValue, includeLastModified);
			stream.writeEndElement();
			stream.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private void write(Page page, XMLStreamWriter stream, ResourceResolver resolver) throws XMLStreamException {

		stream.writeStartElement(NS, "url");
		String loc = externalizer.externalLink(resolver, externalizerDomain, String.format("%s/", page.getPath()));
		loc = loc.replace("http://", "https://");
		writeElement(stream, "loc", loc);
		Page parent = page.getParent();
		Iterator<Page> child = parent.listChildren();
		while (child.hasNext()) {

			String altLoc = externalizer.externalLink(resolver, externalizerDomain,
					String.format("%s/", child.next().getPath()));
			altLoc = altLoc.replace("http://", "https://");
			String[] arr = altLoc.split("/");
			writeAltElement(stream, "link", altLoc, arr[arr.length - 1]);

		}
		final ValueMap properties = page.getProperties();
		writeFirstPropertyValue(stream, "changefreq", changefreqProperties, properties);
		writeFirstPropertyValue(stream, "priority", priorityProperties, properties);
		stream.writeEndElement();
	}

	private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
			final String[] propertyNames, final ValueMap properties) throws XMLStreamException {
		for (String prop : propertyNames) {
			String value = properties.get(prop, String.class);
			if (value != null) {
				writeElement(stream, elementName, value);
				break;
			}
		}
	}

	private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
			throws XMLStreamException {
		stream.writeStartElement(NS, elementName);
		stream.writeCharacters(text);
		stream.writeEndElement();
	}

	private void writeAltElement(final XMLStreamWriter stream, final String elementName, final String text,
			final String name) throws XMLStreamException {
		stream.writeEmptyElement("xhtml", elementName, NS_ALT);
		stream.writeAttribute("rel", "alternate");
		stream.writeAttribute("hreflang", name.toLowerCase());
		stream.writeAttribute("href", text);

	}

}
