package synchronizedBlockd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheData {
    Map<String, String> cache = new HashMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();

    String get(String key){

        lock.readLock().lock();
        if(cache.containsKey(key)) return cache.get(key);
        lock.readLock().unlock();

        return null;
    }

    void put(String key, String val){
        lock.writeLock().lock();
        
        cache.put(key, val);

        lock.writeLock().unlock();
    }
}
