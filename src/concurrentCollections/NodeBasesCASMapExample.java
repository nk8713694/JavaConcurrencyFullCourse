package concurrentCollections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

public class NodeBasesCASMapExample<K, V> {

    private AtomicReferenceArray<Node<K, V>> table;

    private ReentrantLock segmentLocks[];

    private int DEFAULT_CAPACITY = 16;

    public NodeBasesCASMapExample(){
        this.table = new AtomicReferenceArray<>(DEFAULT_CAPACITY);
        this.segmentLocks = new ReentrantLock[DEFAULT_CAPACITY];

        for(int i=0;i< DEFAULT_CAPACITY;i++){
            this.segmentLocks[i] = new ReentrantLock();
        }
    }

    private int hash (K key){
        int h;

        return key == null ? 0 : (h = key.hashCode()) ^ ( h >>> 16);
    }

    public V put(K key, V value){
        int hashValue = hash(key);
        int index = ( table.length() -1 ) & hashValue;

        ReentrantLock reentrantLock = segmentLocks[index];

        try {
            reentrantLock.lock();

            Node<K, V> first = table.get(index);
            for( Node<K, V> e = first ; e!=null ; e = e.next ) {
                if(e.hash == hashValue && e.key == key) {
                    V oldValue = e.value;
                    e.value = value;
                    return oldValue;
                }
            }
            Node<K, V> node = new Node<>(hashValue, key, value, first);
            table.set(index, node);
            return null;
        }finally {
            reentrantLock.unlock();
        }
    }

    public V get(K key){
        int hashValue = hash(key);
        int index = ( table.length() -1 ) & hashValue;
        Node<K, V> first = table.get(index);
        for( Node<K, V> e = first ; e!=null ; e = e.next ) {
            if(e.hash == hashValue && e.key == key) {
                return e.value;
            }
        }
        return null;
    }


    private class Node<K, V> {
        int hash;

        K key;
        V value;

        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
