package com.mystore.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaLocalCache {
    private static LoadingCache<String,String> localCache=
            CacheBuilder.newBuilder().initialCapacity(100).maximumSize(300)
                    .expireAfterWrite(10, TimeUnit.MINUTES)//过期时间
                    .build(
                            new CacheLoader<String, String>() {
                                @Override
                                public String load(String s) throws Exception {
                                    return null;
                                }
                            }
                    );

    //往缓存中放token
    public void setToken(String key,String value){
        localCache.put(key,value);
    }

    //从缓冲中取token
    public String getToken(String key) throws ExecutionException {
        return localCache.get(key);
    }
}
