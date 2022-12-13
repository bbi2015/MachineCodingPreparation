package KeyValueCache;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

class KeyValueCacheActivity<K, V> extends AbstractKeyValueCache<K, V> {

    private final CacheDao<K, V> cacheDao = KeyValueCacheDaoInMemoryImpl.getInstance();

    @Override
    public void initialize(int capacity) {
        cacheDao.initializeCache(capacity);
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
        cacheDao.put(key, value);
    }

    @Override
    public V get(@NonNull K key) {
        return cacheDao.get(key);
    }
}

abstract class AbstractKeyValueCache<K, V> {
    public void initialize(final int capacity) {}
    public void put(@NonNull final K key, @NonNull final V value) {}
    public V get(@NonNull final K key) {
        return null;
    }
}

interface CacheDao<K, V> {
    void initializeCache(int capacity);
    void put(K key, V value);
    V get(K key);
}

class KeyValueCacheDaoInMemoryImpl<K, V> implements CacheDao<K, V> {

    private static final CacheDao CACHE_DAO = new KeyValueCacheDaoInMemoryImpl();

    private int capacity = 0;
    private final List<KeyValuePair<K, V>> cacheData = new ArrayList<>();

    public static<K, V> CacheDao<K, V> getInstance() {
        return CACHE_DAO;
    }

    @Override
    public void initializeCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void put(K key, V value) {
        final KeyValuePair<K, V> currentValue = getCurrentValue(key);
        if (currentValue.getValue() != null) {
            cacheData.remove(currentValue);
        }
        currentValue.setValue(value);
        cacheData.add(0, currentValue);
        this.evictLRUData();
    }

    @Override
    public V get(K key) {
        final KeyValuePair<K, V> value = this.getCurrentValue(key);
        if (value.getValue() == null) {
            System.out.println("Cache doesn't contain the specified key : " + key);
            return null;
        }
        cacheData.remove(value);
        cacheData.add(0, value);
        return value.getValue();
    }

    private KeyValuePair<K, V> getCurrentValue(final K key) {
        return cacheData.stream()
                .filter(kvKeyValuePair -> kvKeyValuePair.getKey().equals(key))
                .findFirst()
                .orElse(KeyValuePair.<K, V>builder().key(key).build());
    }

    private void evictLRUData() {
        if (cacheData.size() > this.capacity) {
            System.out.println("Current size is greater then capacity");
            cacheData.remove(cacheData.size() - 1);
        }
    }
}

@Builder
@Getter
class KeyValuePair<K, V> {
    private final K key;
    private V value;

    public void setValue(final V value) {
        this.value = value;
    }
}

@Builder
@Getter
@EqualsAndHashCode
class Key {
    private final String source;
    private final String dest;
}

@Builder
@Getter
class Value {
    private final String value;
}

public class KeyValueCacheMain {
    public static void main(String[] args) {

        final AbstractKeyValueCache<Key, Value> cache = new KeyValueCacheActivity<>();

        cache.initialize(5);

        cache.put(Key.builder().source("A").dest("B").build(), Value.builder().value("C").build());
        cache.put(Key.builder().source("A").dest("C").build(), Value.builder().value("D").build());
        cache.put(Key.builder().source("A").dest("D").build(), Value.builder().value("E").build());
        cache.put(Key.builder().source("A").dest("E").build(), Value.builder().value("F").build());
        cache.put(Key.builder().source("A").dest("F").build(), Value.builder().value("G").build());

        System.out.println(cache.get(Key.builder().source("A").dest("C").build()).getValue());
    }
}
