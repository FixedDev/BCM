package me.ggamer55.bcm.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Namespace {
    private Map<Class<?>, Map<String, Object>> backing;

    public Namespace() {
        backing = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> clazz, String name) {
        return (T) backing.getOrDefault(clazz, new ConcurrentHashMap<>()).get(name);
    }

    public <T> void setObject(Class<T> clazz, String name, T object) {
        Map<String, Object> map = backing.getOrDefault(clazz, new ConcurrentHashMap<>());
        map.put(name, object);

        backing.put(clazz, map);
    }
}
