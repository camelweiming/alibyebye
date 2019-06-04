package com.abb.bye.service;

import com.abb.bye.client.service.SimpleCache;
import com.google.common.primitives.Longs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 单机版，线上不要用，要替换成redis之类的缓存
 *
 * @author cenpeng.lwm
 * @since 2019/6/4
 */
@Service("simpleCache")
public class LocalSimpleCacheImpl implements SimpleCache {
    private static final Logger logger = LoggerFactory.getLogger(LocalSimpleCacheImpl.class);
    private SimpleCache simpleCache = new SimpleCache(10000);

    @Override
    public Serializable get(String key) throws Exception {
        return (Serializable)simpleCache.get(key);
    }

    @Override
    public List<Serializable> mGet(List<String> keys) throws Exception {
        List<Serializable> list = new ArrayList<>(keys.size());
        for (String k : keys) {
            list.add(get(k));
        }
        return list;
    }

    @Override
    public Serializable getQuietly(String key) {
        try {
            return get(key);
        } catch (Exception e) {
            logger.error("Error getKey:" + key, e);
            return null;
        }
    }

    @Override
    public boolean put(String key, Serializable data, int expireSeconds) throws IOException {
        return simpleCache.put(key, data, expireSeconds);
    }

    @Override
    public boolean putQuietly(String key, Serializable data, int expireSeconds) {
        try {
            return put(key, data, expireSeconds);
        } catch (IOException e) {
            logger.error("Error put:" + key, e);
            return false;
        }
    }

    @Override
    public boolean delete(String key) {
        simpleCache.delete(key);
        return true;
    }

    /**
     * 写着玩的,线上不要用
     */
    public class SimpleCache {
        private DelayQueue delayQueue = new DelayQueue();
        private Map<String, Entity> map = new ConcurrentHashMap<>();
        private final int maxSize;

        public SimpleCache(int maxSize) {
            this.maxSize = maxSize;
            new Thread(new Cleaner()).start();
        }

        public void delete(String key) {
            map.remove(key);
        }

        public boolean put(String key, Object object, int expiredSeconds) {
            if (maxSize > 0 && map.size() > maxSize) {
                logger.debug("cache is full");
                return false;
            }
            Entity entity = new Entity(key, expiredSeconds * 1000 + System.currentTimeMillis(), object);
            map.put(key, entity);
            delayQueue.offer(new DelayedEntity(entity.time, entity.key));
            return true;
        }

        public Object get(String key) {
            Entity entity = map.get(key);
            if (entity == null) {
                return null;
            }
            if (System.currentTimeMillis() > entity.time) {
                map.remove(key);
                return null;
            }
            return entity.object;
        }

        public class Cleaner implements Runnable {
            @Override
            public void run() {

                while (true) {
                    try {
                        DelayedEntity entity = (DelayedEntity)delayQueue.take();
                        map.remove(entity.key);
                        logger.debug("remove:" + entity.key);
                    } catch (Throwable e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
        }

    }

    public static class Entity {
        private String key;
        private long time;
        private Object object;

        public Entity(String key, long time, Object object) {
            this.key = key;
            this.time = time;
            this.object = object;
        }
    }

    public static class DelayedEntity implements Delayed {
        private long time;
        private String key;

        public DelayedEntity(long time, String key) {
            this.time = time;
            this.key = key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (o instanceof DelayedEntity) {
                return Longs.compare(time, ((DelayedEntity)o).time);
            }
            return 0;
        }
    }
}


