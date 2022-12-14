package KeyValueCache;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public <T> void createSecondaryIndex(String fieldName, T classType) {
        cacheDao.createSecondaryIndex(fieldName, classType);
    }

    @Override
    public <T> V getFromSI(T key) {
        return cacheDao.getFromSI("email", key);
    }
}

abstract class AbstractKeyValueCache<K, V> {
    public void initialize(final int capacity) {}
    public void put(@NonNull final K key, @NonNull final V value) {}
    public V get(@NonNull final K key) {
        return null;
    }
    public <T> void createSecondaryIndex(final String fieldName, T classType) {};
    public <T> V getFromSI(final T key) {
        return null;
    };
}

interface CacheDao<K, V> {
    void initializeCache(int capacity);
    void put(K key, V value);
    V get(K key);
    <T> void createSecondaryIndex(String fieldName, T classType);
    <T> V getFromSI(String indexName, T key);
}

interface SecondaryIndex<T, K, V> {
    void createSecondaryIndex(String fieldName, List<KeyValuePair<K, V>> value);
    void put(T key, K pointer);
    K get(T key);
    void delete(T key);
}

class SecondaryIndexInMemoryImpl<T, K, V> implements SecondaryIndex<T, K, V> {

    private static final SecondaryIndex SECONDARY_INDEX = new SecondaryIndexInMemoryImpl();

    private final List<SecondaryIndexModel<T, K>> secondaryIndexModels = new ArrayList<>();

    public static<T, K, V> SecondaryIndex<T, K, V> getInstance() {
        return SECONDARY_INDEX;
    }

    @Override
    public void createSecondaryIndex(String fieldName, List<KeyValuePair<K, V>> value) {
        value.forEach(kvKeyValuePair -> {
            try {
                final Field field = kvKeyValuePair.getValue().getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                secondaryIndexModels.add(SecondaryIndexModel.<T, K>builder()
                                .key((T) field.get(kvKeyValuePair.getValue()))
                                .pointer(kvKeyValuePair.getKey())
                        .build());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void put(T key, K pointer) {
        secondaryIndexModels.add(SecondaryIndexModel.<T, K>builder()
                        .key(key)
                        .pointer(pointer)
                .build());
    }

    @Override
    public K get(T key) {
        return secondaryIndexModels.stream()
                .filter(tkSecondaryIndexModel -> tkSecondaryIndexModel.getKey().equals(key))
                .findFirst()
                .map(SecondaryIndexModel::getPointer)
                .orElse(null);
    }

    @Override
    public void delete(T key) {
        final SecondaryIndexModel<T, K> secondaryIndexModel = secondaryIndexModels
                .stream()
                .filter(tkSecondaryIndexModel -> tkSecondaryIndexModel.getKey().equals(key))
                .findFirst()
                .get();
        secondaryIndexModels.remove(secondaryIndexModel);
    }
}

class KeyValueCacheDaoInMemoryImpl<K, V> implements CacheDao<K, V> {

    private static final CacheDao CACHE_DAO = new KeyValueCacheDaoInMemoryImpl();

    private int capacity = 0;
    private final List<KeyValuePair<K, V>> cacheData = new ArrayList<>();
    private final Map<String, SecondaryIndex> secondaryIndexMap = new HashMap<>();

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

    @Override
    public <T> void createSecondaryIndex(String fieldName, T classType) {
        final SecondaryIndex<T, K, V> secondaryIndex = SecondaryIndexInMemoryImpl.getInstance();
        this.secondaryIndexMap.put(fieldName, secondaryIndex);
        secondaryIndex.createSecondaryIndex(fieldName, cacheData);
    }

    @Override
    public <T> V getFromSI(String indexName, T key) {
        return get((K) secondaryIndexMap.get(indexName).get(key));
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
class SecondaryIndexModel<T, K> {
    private final T key;
    private final K pointer;
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
class Value {
    private final String userId;
    private final String firstName;
    private final String email;
    private final String lastName;
}

public class KeyValueCacheMain {
    public static void main(String[] args) {

        final AbstractKeyValueCache<String, Value> cache = new KeyValueCacheActivity<>();

        cache.initialize(5);

        cache.put("1", Value.builder().userId("1").firstName("Arijit").lastName("Debnath").email("adn").build());
        cache.put("2", Value.builder().userId("2").firstName("Pulkit").lastName("Agarwal").email("pagg").build());

        System.out.println(cache.get("1").getLastName());

        cache.createSecondaryIndex("email", String.class);
        System.out.println(cache.getFromSI("adn").getFirstName());
    }
}
