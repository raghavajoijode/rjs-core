package com.subra.aem.rjs.cache.ehcache.helpers.impl;

import com.subra.aem.rjs.cache.ehcache.helpers.CacheHelper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class CacheHelperImpl<V> implements CacheHelper<V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheHelperImpl.class);

    private String cacheName;
    private Cache cache;
    private long miss;
    private long hits;
    private long gets;
    private CacheManager cacheManager;

    public CacheHelperImpl(final CacheManager manager, final String name) {
        if (name == null) {
            cacheName = "default";
        } else {
            cacheName = name;
        }
        this.cacheManager = manager;
        synchronized (cacheManager) {
            cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                cacheManager.addCache(cacheName);
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    throw new RJSRuntimeException("Failed to create SubraCache with name " + cacheName);
                }
            }
        }
    }

    public void clear() {
        cache.removeAll();
    }

    public boolean containsKey(final String key) {
        return cache.isKeyInCache(key);
    }

    public V get(final String key) {
        final Element e = cache.get(key);
        if (e == null) {
            return stats(null);
        }
        return stats(e.getObjectValue());
    }

    private V stats(final Object objectValue) {
        if (objectValue == null) {
            miss++;
        } else {
            hits++;
        }
        gets++;
        if (gets % 1000 == 0) {
            final long hp = (100 * hits) / gets;
            final long mp = (100 * miss) / gets;
            LOGGER.info("{} SubraCache Stats hits {} ({}%), misses {} ({}%), calls {}", cacheName, hits, hp, miss, mp,
                    gets);
        }
        return (V) objectValue;
    }

    public V put(final String key, final V payload) {
        V previous = null;
        if (cache.isKeyInCache(key)) {
            final Element e = cache.get(key);
            if (e != null) {
                previous = (V) e.getObjectValue();
            }
        }
        cache.put(new Element(key, payload));
        return previous;
    }

    public boolean remove(final String key) {
        return cache.remove(key);
    }

    public void removeChildren(String key) {
        cache.remove(key);
        if (!key.endsWith("/")) {
            key = key + "/";
        }
        final List<?> keys = cache.getKeys();
        for (final Object k : keys) {
            if (((String) k).startsWith(key)) {
                cache.remove(k);
            }
        }
    }

    public Collection<V> values() {
        final List<String> keys = cache.getKeys();
        final List<V> values = new ArrayList<>();
        for (final String k : keys) {
            final Element e = cache.get(k);
            if (e != null) {
                values.add((V) e.getObjectValue());
            }
        }
        return values;
    }

    public Collection<String> keys() {
        return cache.getKeys();
    }

}