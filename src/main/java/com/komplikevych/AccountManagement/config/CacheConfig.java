package com.komplikevych.AccountManagement.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    private static final long CACHE_TTL_MS = 10 * 60 * 1000; // 10 minutes
    private final ConcurrentMap<String, CacheEntry> cacheEntries = new ConcurrentHashMap<>();
    private CustomCache customCache;

    @Bean
    @SuppressWarnings("null")
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        customCache = new CustomCache("topUsers");
        List<Cache> caches = Arrays.asList(customCache);
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    public class CustomCache extends ConcurrentMapCache {
        public CustomCache(String name) {
            super(name);
        }

        @Override
        @Nullable
        public ValueWrapper get(@NonNull Object key) {
            CacheEntry entry = cacheEntries.get(key.toString());
            if (entry == null) {
                ValueWrapper wrapper = super.get(key);
                if (wrapper != null) {
                    cacheEntries.put(key.toString(), new CacheEntry(wrapper.get(), System.currentTimeMillis()));
                }
                return wrapper;
            }
            
            if (System.currentTimeMillis() - entry.timestamp > CACHE_TTL_MS) {
                cacheEntries.remove(key.toString());
                evict(key);
                return null;
            }
            
            return super.get(key);
        }

        @Override
        public void put(@NonNull Object key, @Nullable Object value) {
            cacheEntries.put(key.toString(), new CacheEntry(value, System.currentTimeMillis()));
            super.put(key, value);
        }

        @Override
        public void evict(@NonNull Object key) {
            cacheEntries.remove(key.toString());
            super.evict(key);
        }

        @Override
        public void clear() {
            cacheEntries.clear();
            super.clear();
        }
    }

    @Scheduled(fixedRate = 60000)
    public void evictExpiredEntries() {
        if (customCache == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        cacheEntries.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().timestamp > CACHE_TTL_MS;
            if (expired) {
                customCache.evict((Object) entry.getKey());
            }
            return expired;
        });
    }

    @SuppressWarnings("unused")
    private static class CacheEntry {
        @Nullable
        final Object value;
        final long timestamp;

        CacheEntry(@Nullable Object value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
