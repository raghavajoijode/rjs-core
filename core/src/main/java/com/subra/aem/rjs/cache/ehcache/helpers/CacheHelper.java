package com.subra.aem.rjs.cache.ehcache.helpers;

import java.util.Collection;

public interface CacheHelper<V> {

    V put(String key, V payload);

    boolean containsKey(String key);

    V get(String key);

    void clear();

    boolean remove(String key);

    void removeChildren(String key);

    Collection<V> values();

    Collection<String> keys();

}