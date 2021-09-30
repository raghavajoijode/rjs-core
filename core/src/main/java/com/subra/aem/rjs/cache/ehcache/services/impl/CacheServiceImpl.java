package com.subra.aem.rjs.cache.ehcache.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.subra.aem.rjs.cache.ehcache.helpers.impl.CacheHelperImpl;
import com.subra.aem.rjs.cache.ehcache.services.CacheService;
import com.subra.aem.rjs.cache.ehcache.helpers.CacheHelper;
import com.subra.aem.rjs.core.UserMapperService;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component(service = CacheService.class, immediate = true)
@ServiceDescription("CacheService Service")
@Designate(ocd = CacheService.Config.class)
public class CacheServiceImpl implements CacheService {

    public static final String CLASS_COUNTRY_FORM = "%s:%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceImpl.class);
    private static final Map<String, Object> AUTH_USER_INFO;

    static {
        AUTH_USER_INFO = new HashMap<>();
        AUTH_USER_INFO.put(ResourceResolverFactory.SUBSERVICE, UserMapperService.ADMIN_SERVICE.value());
    }

    private final Map<String, CacheHelper<?>> caches = new ConcurrentHashMap<>();
    protected CacheManager cacheManager;
    @Reference
    private ResourceResolverFactory resolverFactory;

    public CacheServiceImpl() throws IOException {
        super();
    }

    @Activate
    protected void activate(final Config config) {
        String configPath = config.cacheConfig();
        LOGGER.info("Activating using jcr config location {}", configPath);
        try (InputStream inputStream = buildConfigStream(configPath)) {
            cacheManager = CacheManager.create(inputStream);
        } catch (CacheException | IOException e) {
            LOGGER.error("Exception trying to build cacheManager from config", e);
            cacheManager = new CacheManager();
        }
    }

    @Modified
    protected void modified(final Config config) {
        deactivate();
        activate(config);
    }

    @Deactivate
    protected void deactivate() {
        if (cacheManager != null) {
            cacheManager.shutdown();
            cacheManager = null;
        }
    }

    @Override
    public List<Ehcache> getEhcacheInstances() {
        String[] cacheNames = cacheManager.getCacheNames();
        if (cacheNames == null) {
            return Collections.emptyList();
        }

        List<Ehcache> ehcacheList = new ArrayList<>();
        for (String cacheName : cacheNames) {
            Ehcache c = cacheManager.getEhcache(cacheName);
            if (c != null) {
                ehcacheList.add(c);
            }
        }

        return ehcacheList;
    }

    @Override
    public <V> CacheHelper<V> getInstanceCache(final String className, final String countryCode) {
        return getInstanceCache(name(className, countryCode));
    }

    @SuppressWarnings("unchecked")
    private <V> CacheHelper<V> getInstanceCache(final String name) {
        return (CacheHelper<V>) caches.computeIfAbsent(name, k -> new CacheHelperImpl<>(cacheManager, name));
    }

    public void clearByClassName(final String className) {
        for (Map.Entry<String, CacheHelper<?>> cache : caches.entrySet()) {
            if (StringUtils.startsWith(cache.getKey(), className))
                cache.getValue().clear();
        }
    }

    protected Session getSession() throws LoginException {
        ResourceResolver resolver = resolverFactory.getServiceResourceResolver(AUTH_USER_INFO);
        return resolver.adaptTo(Session.class);
    }

    protected InputStream buildConfigStream(final String path) {
        try {
            Node configNode = getSession().getNode(path).getNode(JcrConstants.JCR_CONTENT);
            Property configData = configNode.getProperty(JcrConstants.JCR_DATA);
            LOGGER.debug("Activating EHCache with config: {}", configData.getString());
            return configData.getBinary().getStream();
        } catch (RepositoryException | LoginException e) {
            LOGGER.error("Exception while trying to read config node", e);
            return null;
        }
    }

    private String name(final String className, final String countryCode) {
        return String.format(CLASS_COUNTRY_FORM, className, countryCode);
    }

}
