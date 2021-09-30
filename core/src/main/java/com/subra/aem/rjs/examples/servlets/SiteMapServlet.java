package com.subra.aem.rjs.examples.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.*;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class, factory = "org.subra.aem.example.sample.servlets.SiteMapServlet", configurationPolicy = ConfigurationPolicy.REQUIRE, property = {
		SLING_SERVLET_RESOURCE_TYPES + "=sling/servlet/default", SLING_SERVLET_EXTENSIONS + "=xml",
		SLING_SERVLET_METHODS + "=GET", SLING_SERVLET_SELECTORS + "=sitemap", "webconsole.configurationFactory.nameHint"
				+ "=" + "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]" })
@Designate(ocd = SiteMapServlet.Config.class)
public final class SiteMapServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

	private static final boolean DEFAULT_INCLUDE_LAST_MODIFIED = false;

	private static final boolean DEFAULT_INCLUDE_INHERITANCE_VALUE = false;

	private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";
	private static final String CHANGE_FREQUENCY = "changefreq";
	private static final String PRIORITY = "priority";

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

	private static final String PROP_DAM_ASSETS_PROPERTY = "damassets.property";

	private static final String PROP_DAM_ASSETS_TYPES = "damassets.types";

	private static final String PROP_EXCLUDE_FROM_SITEMAP_PROPERTY = "exclude.property";

	private static final String PROP_INCLUDE_INHERITANCE_VALUE = "include.inherit";

	private static final String PROP_EXTENSIONLESS_URLS = "extensionless.urls";

	private static final String PROP_REMOVE_TRAILING_SLASH = "remove.slash";

	private static final String PROP_CHARACTER_ENCODING_PROPERTY = "character.encoding";

	private static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

	private static final String NS_IMAGE = "http://www.google.com/schemas/sitemap-image/1.1";

	private static final String NS_VIDEO = "http://www.google.com/schemas/sitemap-video/1.1";

	@Reference
	private transient Externalizer externalizer;

	private String externalizerDomain;

	private boolean includeInheritValue;

	private boolean includeLastModified;

	private String[] changefreqProperties;

	private String[] priorityProperties;

	private String damAssetProperty;

	private List<String> damAssetTypes;

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
		this.damAssetProperty = PropertiesUtil.toString(properties.get(PROP_DAM_ASSETS_PROPERTY), "");
		this.damAssetTypes = Arrays
				.asList(PropertiesUtil.toStringArray(properties.get(PROP_DAM_ASSETS_TYPES), new String[0]));
		this.excludeFromSiteMapProperty = PropertiesUtil.toString(properties.get(PROP_EXCLUDE_FROM_SITEMAP_PROPERTY),
				NameConstants.PN_HIDE_IN_NAV);
		this.characterEncoding = PropertiesUtil.toString(properties.get(PROP_CHARACTER_ENCODING_PROPERTY), null);
		this.extensionlessUrls = PropertiesUtil.toBoolean(properties.get(PROP_EXTENSIONLESS_URLS),
				DEFAULT_EXTENSIONLESS_URLS);
		this.removeTrailingSlash = PropertiesUtil.toBoolean(properties.get(PROP_REMOVE_TRAILING_SLASH),
				DEFAULT_REMOVE_TRAILING_SLASH);
	}

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType(request.getResponseContentType());
		if (StringUtils.isNotEmpty(this.characterEncoding)) {
			response.setCharacterEncoding(characterEncoding);
		}
		ResourceResolver resourceResolver = request.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = pageManager.getContainingPage(request.getResource());

		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		try {
			XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
			stream.writeStartDocument("1.0");

			stream.writeStartElement("", "urlset", NS);
			stream.writeNamespace("", NS);
			stream.writeNamespace("image", NS_IMAGE);
			stream.writeNamespace("video", NS_VIDEO);
			// first do the current page
			write(page, stream, resourceResolver);

			for (Iterator<Page> children = page.listChildren(new PageFilter(false, true), true); children.hasNext();) {
				write(children.next(), stream, resourceResolver);
			}

			if (!damAssetTypes.isEmpty() && damAssetProperty.length() > 0) {
				for (Resource assetFolder : getAssetFolders(page, resourceResolver)) {
					writeAssets(stream, assetFolder, resourceResolver);
				}
			}

			stream.writeEndElement();

			stream.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	private Collection<Resource> getAssetFolders(Page page, ResourceResolver resolver) {
		List<Resource> allAssetFolders = new ArrayList<>();
		ValueMap properties = page.getProperties();
		String[] configuredAssetFolderPaths = properties.get(damAssetProperty, String[].class);
		if (configuredAssetFolderPaths != null) {
			// Sort to aid in removal of duplicate paths.
			Arrays.sort(configuredAssetFolderPaths);
			String prevPath = "#";
			for (String configuredAssetFolderPath : configuredAssetFolderPaths) {
				// Ensure that this folder is not a child folder of another
				// configured folder, since it will already be included when
				// the parent folder is traversed.
				if (StringUtils.isNotBlank(configuredAssetFolderPath) && !configuredAssetFolderPath.equals(prevPath)
						&& !StringUtils.startsWith(configuredAssetFolderPath, prevPath + "/")) {
					Resource assetFolder = resolver.getResource(configuredAssetFolderPath);
					if (assetFolder != null) {
						prevPath = configuredAssetFolderPath;
						allAssetFolders.add(assetFolder);
					}
				}
			}
		}
		return allAssetFolders;
	}

	private void write(Page page, XMLStreamWriter stream, ResourceResolver resolver) throws XMLStreamException {
		if (isHidden(page)) {
			return;
		}
		stream.writeStartElement(NS, "url");
		String loc = "";

		if (!extensionlessUrls) {
			loc = externalizer.externalLink(resolver, externalizerDomain, String.format("%s.html", page.getPath()));
		} else {
			String urlFormat = removeTrailingSlash ? "%s" : "%s/";
			loc = externalizer.externalLink(resolver, externalizerDomain, String.format(urlFormat, page.getPath()));
		}

		writeElement(stream, "loc", loc);

		if (includeLastModified) {
			Calendar cal = page.getLastModified();
			if (cal != null) {
				writeElement(stream, "lastmod", DATE_FORMAT.format(cal));
			}
		}

		if (includeInheritValue) {
			HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
					page.getContentResource());
			writeFirstPropertyValue(stream, CHANGE_FREQUENCY, changefreqProperties, hierarchyNodeInheritanceValueMap);
			writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);
		} else {
			ValueMap properties = page.getProperties();
			writeFirstPropertyValue(stream, CHANGE_FREQUENCY, changefreqProperties, properties);
			writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);
		}

		stream.writeEndElement();
	}

	private boolean isHidden(final Page page) {
		return page.getProperties().get(this.excludeFromSiteMapProperty, false);
	}

	private void writeAsset(Asset asset, XMLStreamWriter stream, ResourceResolver resolver) throws XMLStreamException {
		stream.writeStartElement(NS, "url");

		String loc = externalizer.externalLink(resolver, externalizerDomain, asset.getPath());
		writeElement(stream, "loc", loc);

		if (includeLastModified) {
			long lastModified = asset.getLastModified();
			if (lastModified > 0) {
				writeElement(stream, "lastmod", DATE_FORMAT.format(lastModified));
			}
		}

		Resource contentResource = asset.adaptTo(Resource.class).getChild(JcrConstants.JCR_CONTENT);
		if (contentResource != null) {
			if (includeInheritValue) {
				HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
						contentResource);
				writeFirstPropertyValue(stream, CHANGE_FREQUENCY, changefreqProperties,
						hierarchyNodeInheritanceValueMap);
				writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);
			} else {
				ValueMap properties = contentResource.getValueMap();
				writeFirstPropertyValue(stream, CHANGE_FREQUENCY, changefreqProperties, properties);
				writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);
			}
		}

		stream.writeEndElement();
	}

	private void writeAssets(final XMLStreamWriter stream, final Resource assetFolder, final ResourceResolver resolver)
			throws XMLStreamException {
		for (Iterator<Resource> children = assetFolder.listChildren(); children.hasNext();) {
			Resource assetFolderChild = children.next();
			if (assetFolderChild.isResourceType(DamConstants.NT_DAM_ASSET)) {
				Asset asset = assetFolderChild.adaptTo(Asset.class);

				if (damAssetTypes.contains(asset.getMimeType())) {
					writeAsset(asset, stream, resolver);
				}
			} else {
				writeAssets(stream, assetFolderChild, resolver);
			}
		}
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

	private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
			final String[] propertyNames, final InheritanceValueMap properties) throws XMLStreamException {
		for (String prop : propertyNames) {
			String value = properties.get(prop, String.class);
			if (value == null) {
				value = properties.getInherited(prop, String.class);
			}
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

}
