package concurrentCollections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SegmentationHashMapExample<K, V> {

    private Segment<K, V> segments[];
    private int segmentaionCount;

    public SegmentationHashMapExample(int segmentaionCount) {
        this.segmentaionCount = segmentaionCount;
        this.segments = (Segment<K, V>[]) new Segment[segmentaionCount];

        for(int i=0;i< segmentaionCount;i++){
            segments[i] = new Segment<>();
        }
    }

    private int hash(K key){
        return key.hashCode() % segmentaionCount;
    }

    public V put(K key, V value){
        int segmenationIndex = hash(key);
        return segments[segmenationIndex].put(key, value);
    }

    public V get(K key){
        int segmenationIndex = hash(key);
        return segments[segmenationIndex].get(key);
    }

    private static class Segment<K, V> {
        private Map<K, V> map = new HashMap<>();
        private ReentrantLock lock = new ReentrantLock();

        public V put(K key, V value){
            try {
                lock.lock();
                return map.put(key, value);
            }finally {
                lock.unlock();
            }
        }
        public V get(K key){
            return map.get(key);
        }
    }

    public static void main(String[] args) {

        SegmentationHashMapExample segMap = new SegmentationHashMapExample(10);

        segMap.put(1, "Hello");
        segMap.put(15, "World");


        System.out.println(segMap.get(1));
        System.out.println(segMap.get(15));
    }
}
