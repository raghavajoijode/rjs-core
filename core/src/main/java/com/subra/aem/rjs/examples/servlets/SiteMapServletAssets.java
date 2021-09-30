package com.subra.aem.rjs.examples.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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
import org.subra.commons.constants.HttpType;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class, factory = "org.subra.aem.example.sample.servlets.CustomSiteMapServletAssets", configurationPolicy = ConfigurationPolicy.REQUIRE, property = {
		SLING_SERVLET_RESOURCE_TYPES + "=sling/servlet/default", SLING_SERVLET_EXTENSIONS + "=xml",
		SLING_SERVLET_METHODS + "=GET", SLING_SERVLET_SELECTORS + "=sitemap-assets",
		"webconsole.configurationFactory.nameHint" + "="
				+ "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]" })
@Designate(ocd = SiteMapServletAssets.Config.class)
public class SiteMapServletAssets extends SlingSafeMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapServletAssets.class);
	private static final long serialVersionUID = 1L;

	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	private static final boolean DEFAULT_INCLUDE_LAST_MODIFIED = false;

	private static final boolean DEFAULT_INCLUDE_INHERITANCE_VALUE = false;

	private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";

	private static final String CHANGE_FREQUENCY = "changefreq";
	private static final String PRIORITY = "priority";
	private static final String VIDEO = "video";
	private static final String IMAGE = "image";
	private static final String DC_TITLE = "dc:title";

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

	private static final String PROP_EXTENSIONLESS_URLS = "extensionless.urls";

	private static final String PROP_REMOVE_TRAILING_SLASH = "remove.slash";

	private static final String PROP_CHARACTER_ENCODING_PROPERTY = "character.encoding";

	private static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

	private static final String NS_IMAGE = "http://www.google.com/schemas/sitemap-image/1.1";

	private static final String NS_VIDEO = "http://www.google.com/schemas/sitemap-video/1.1";

	@Reference
	private transient Externalizer externalizer;

	private String externalizerDomain;

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
		LOGGER.debug("Unused Values are {}, {}, {} {}", excludeFromSiteMapProperty, extensionlessUrls,
				removeTrailingSlash, includeLastModified);
	}

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("Unused Values are {}, {}, {} {}", excludeFromSiteMapProperty, extensionlessUrls,
				removeTrailingSlash, includeLastModified);
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
			stream.writeNamespace(IMAGE, NS_IMAGE);
			stream.writeNamespace(VIDEO, NS_VIDEO);

			// first do the current page
			write(page, stream, resourceResolver);

			for (Iterator<Page> children = page.listChildren(new PageFilter(), true); children.hasNext();) {
				if (isNotCustomPage(page)) {
					write(children.next(), stream, resourceResolver);
				}
			}
			stream.writeEndElement();
			stream.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private boolean isNotCustomPage(Page page) {
		return !(page.getName().equals("industries") || page.getName().equals("service-lines")
				|| page.getName().equals("case-study") || page.getName().equals("latest-thinking")
				|| page.getName().equals("offerings") || page.getName().equals("solutions")
				|| page.getName().equals("segments"));
	}

	private void write(Page page, XMLStreamWriter stream, ResourceResolver resolver)
			throws XMLStreamException {

		if (isNotCustomPage(page)) {

			stream.writeStartElement(NS, "url");
			String loc = externalizer.externalLink(resolver, externalizerDomain, String.format("%s/", page.getPath()));
			loc = loc.replaceAll(HttpType.HTTP_PROTOCOL.value(), HttpType.HTTPS_PROTOCOL.value());
			if (loc.contains("case-study")) {
				loc = loc.replace("/case-study/", "/");
			} else if (loc.contains("latest-thinking")) {
				loc = loc.replace("/latest-thinking/", "/");
			} else if (loc.contains("offerings")) {
				loc = loc.replace("/offerings/", "/");
			} else if (loc.contains("solutions")) {
				loc = loc.replace("/solutions/", "/");
			} else if (loc.contains("segments")) {
				loc = loc.replace("/segments/", "/");
			} else if (loc.contains("industries")) {
				loc = loc.replace("/industries/", "/");
			} else if (loc.contains("service-lines")) {
				loc = loc.replace("/service-lines/", "/");
			}
			loc = loc.replace(HttpType.HTTP_PROTOCOL.value(), HttpType.HTTPS_PROTOCOL.value());
			writeElement(stream, "loc", "", loc);
			if (includeLastModified) {
				Calendar cal = page.getLastModified();
				if (cal != null) {
					writeElement(stream, "lastmod", "", DATE_FORMAT.format(cal));
				}

			}
			writeImageAndVideoElements(page, stream, resolver);

			final ValueMap properties = page.getProperties();
			writeFirstPropertyValue(stream, CHANGE_FREQUENCY, changefreqProperties, properties);
			writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);

			stream.writeEndElement();
		}
	}

	private void writeImageAndVideoElements(Page page, XMLStreamWriter stream, ResourceResolver resolver)
			throws XMLStreamException {
		Resource resource = resolver.getResource(page.getPath() + "/" + JcrConstants.JCR_CONTENT);
		Node node = resource.adaptTo(Node.class);
		AssetReferenceSearch assetReference = new AssetReferenceSearch(node, "/content/dam", resolver);
		for (Map.Entry<String, Asset> assetMap : assetReference.search().entrySet()) {
			Asset assetFromPage = assetMap.getValue();
			if (assetFromPage.getMimeType().contains(IMAGE)) {
				writeImageElement(stream, resolver, IMAGE, assetFromPage);
			}
			if (assetFromPage.getMimeType().contains(VIDEO)) {
				writeVideoElement(stream, resolver, VIDEO, assetFromPage);
			}
		}
	}

	private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
			final String[] propertyNames, final ValueMap properties) throws XMLStreamException {
		for (String prop : propertyNames) {
			String value = properties.get(prop, String.class);
			if (value != null) {
				writeElement(stream, elementName, "", value);
				break;
			}
		}
	}

	private void writeElement(final XMLStreamWriter stream, final String elementName, final String prifix,
			final String text) throws XMLStreamException {
		stream.writeStartElement(prifix, elementName, NS);
		stream.writeCharacters(text);
		stream.writeEndElement();
	}

	private void writeImageElement(final XMLStreamWriter stream, ResourceResolver resolver, final String elementName,
			final Asset asset) throws XMLStreamException {
		stream.writeStartElement(IMAGE, elementName, NS_IMAGE);
		String loc = externalizer.externalLink(resolver, externalizerDomain, asset.getPath());
		loc = loc.replace(HttpType.HTTP_PROTOCOL.value(), HttpType.HTTPS_PROTOCOL.value());
		writeElement(stream, "loc", IMAGE, loc);
		String title = asset.getMetadataValue(DC_TITLE);
		if (title != null && !(title.equals(""))) {
			writeElement(stream, "title", IMAGE, title);
		}
		stream.writeEndElement();
	}

	private void writeVideoElement(final XMLStreamWriter stream, ResourceResolver resolver, final String elementName,
			final Asset asset) throws XMLStreamException {
		stream.writeStartElement(VIDEO, elementName, NS_VIDEO);

		String loc = externalizer.externalLink(resolver, externalizerDomain, asset.getPath());
		loc = loc.replace(HttpType.HTTP_PROTOCOL.value(), HttpType.HTTPS_PROTOCOL.value());
		writeElement(stream, "content_loc", VIDEO, loc);
		String title = asset.getMetadataValue(DC_TITLE) != null ? asset.getMetadataValue(DC_TITLE)
				: asset.getName();
		writeElement(stream, "title", VIDEO, title);
		String description = asset.getMetadataValue("dc:description") != null ? asset.getMetadataValue("dc:description")
				: "NA";
		writeElement(stream, "description", VIDEO, description);
		String imageFromDam = asset.getMetadataValue("tile-image") != null ? asset.getMetadataValue("tile-image")
				: asset.getMetadataValue("dc:image");
		String videoImage = imageFromDam != null && !(imageFromDam.equals("")) ? imageFromDam
				: "/etc/designs/demo/images/header/abc-logo.jpg";
		String videoImageLoc = externalizer.externalLink(resolver, externalizerDomain, videoImage);
		videoImageLoc = videoImageLoc.replace("http://", "https://");
		writeElement(stream, "thumbnail_loc", VIDEO, videoImageLoc);
		stream.writeEndElement();
	}

}
