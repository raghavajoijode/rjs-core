package com.subra.aem.rjs.examples.listeners;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A component that will listen to the /etc/commerce/special-products/ioa nodes
 * and create/update pages with the IOA product number.
 */
@Component(service = EventListener.class, immediate = true, enabled = false,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to Event Listener listen on changes in the resource tree"
        })
@Designate(ocd = SimpleListener.Config.class)
public class SimpleListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleListener.class);

    private static final String IOA_PATH = "ioaPath";
    private static final String CONTENT_PAGES = "contentPages";
    private static final String IROA_TEMPLATE_SUFFIX = "/iroa";

    private static final Pattern IOA_PATH_PATTERN = Pattern.compile("^/?(\\w+)/productNumber$");

    protected String ioaPath;

    protected String contentPagesConfig;
    protected ObservationManager observationManager;
    protected Map<String, String> contentPages; // Map path->template
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private Replicator replicator;

    @Activate
    protected void activate(final Config config) {
        ioaPath = config.ioaPath();
        contentPagesConfig = config.contentPagesConfig();
        contentPages = Stream.of(StringUtils.split(contentPagesConfig, ",")).collect(Collectors.toMap(c -> c.replaceFirst("\\|.*", ""), c -> c.contains("|") ? c.replaceFirst(".*\\|", "") : ""));
        LOGGER.info("Activated with ioaPath={}, contentPagesConfig={}", ioaPath, contentPagesConfig);

        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resolverFactory)) {
            Session session = resourceResolver.adaptTo(Session.class);
            observationManager = session.getWorkspace().getObservationManager();
            observationManager.addEventListener(this, Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, ioaPath, true, null, null, true);
        } catch (Exception e) {
            LOGGER.error("Can not access the JCR to register event listener", e);
        }
    }

    @Deactivate
    protected void deactivate(final ComponentContext componentContext) {
        try {
            if (observationManager != null) {
                observationManager.removeEventListener(this);
            }
        } catch (RepositoryException e) {
            LOGGER.error("error removing the JCR event listener", e);
        }
    }

    @Override
    public void onEvent(final EventIterator events) {
        while (events.hasNext()) {
            Event event = events.nextEvent();
            try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resolverFactory)) {
                String relativePath = StringUtils.removeStart(ObjectUtils.defaultIfNull(event.getPath(), ""), ioaPath);
                Matcher m = IOA_PATH_PATTERN.matcher(relativePath);
                if (m.matches()) {
                    String channel = m.group(1);
                    Session session = resourceResolver.adaptTo(Session.class);
                    String productNumber = session.getProperty(event.getPath()).getString();
                    LOGGER.debug("Got IOA event for channel={} product={}", channel, productNumber);
                    contentPages.forEach((key, value) -> updateContentPage(resourceResolver, productNumber, channel, key, value));
                } else {
                    LOGGER.trace("Got unsupported path from JCR event: {} (relative={})", event.getPath(),
                            relativePath);
                }
            } catch (Exception e) {
                LOGGER.error("Error updating content page", e);
            }
        }
    }

    private void updateContentPage(final ResourceResolver resourceResolver, final String productNumber, final String channelCode, final String contentPath, final String templatePath) {
        try {
            if (StringUtils.isNotBlank(templatePath)) {
                boolean isMultiChannel = templatePath.contains(IROA_TEMPLATE_SUFFIX);
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                String path = contentPath + (isMultiChannel ? "" : (CommonHelper.SLASH + channelCode));
                Page page = pageManager.getPage(path);

                page = validateOrCreatePage(channelCode, contentPath, templatePath, pageManager, path, page);
                if (page == null) return;
                RJSResourceUtils.addOrUpdateProperty(page.getContentResource(), "prodNumber" + (isMultiChannel ? '-' + channelCode : ""), productNumber);
                replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, path);
                LOGGER.debug("Set product number of '{}' to {}.", path, productNumber);
            } else {
                LOGGER.warn("TemplatePath for contentPath: {} not found!", contentPath);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create page", e);
        }
    }

    private Page validateOrCreatePage(String channelCode, String contentPath, String templatePath, PageManager pageManager, String path, Page page) throws WCMException {
        if (page != null) {
            Template template = page.getTemplate();
            if (template == null) {
                LOGGER.error("no template found for '{}'.", path);
                return null;
            }
            String oldTemplatePath = template.getPath();
            if (!templatePath.equalsIgnoreCase(oldTemplatePath)) {
                LOGGER.error("wrong template found! '{}' instead of expected '{}'.", oldTemplatePath, templatePath);
                return null;
            }
        } else {
            LOGGER.debug("Create a new page under: {} with channelCode: {}", contentPath, channelCode);
            page = pageManager.create(contentPath, channelCode, templatePath, channelCode);
        }
        return page;
    }

    @ObjectClassDefinition(name = "Demo Sample Event Listner Event Listiner", description = "Service for ehcache configuration")
    public @interface Config {
        @AttributeDefinition(name = IOA_PATH, description = "Base path for special products")
        String ioaPath() default "/etc/commerce/special-products/ioa/us";

        @AttributeDefinition(name = CONTENT_PAGES, description = "Comma-separated list of pages to write. Optional with the template in the format <path>|<template>", defaultValue = "/content/subra/en/catalog/products/specials|/apps/subra-commerce/templates/live-tv-on-air")
        String contentPagesConfig();
    }

}
