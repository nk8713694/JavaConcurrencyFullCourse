package concurrentCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CustomCopyOnWriteArrayList<E> {

    private Object[] array;
    private ReentrantLock lock = new ReentrantLock();

    public CustomCopyOnWriteArrayList(){
        array = new Object[0];
    }

    public int size(){
        return array.length;
    }

    public void add(E element){
        try {
            lock.lock();
            Object[] newArray = Arrays.copyOf(array, array.length + 1);
            newArray[array.length] = element;
            array = newArray;
        }finally {
            lock.unlock();
        }
    }

    public void set(int index , E element){
        if(index < array.length) return;// throw exception
        try {
            lock.lock();
            Object[] newArray = Arrays.copyOf(array, array.length );
            newArray[index] = element;
            array = newArray;
        }finally {
            lock.unlock();
        }
    }

    public void remove(int index){
        if(index < array.length) return;// throw exception
        try {
            lock.lock();
            Object[] newArray = Arrays.copyOf(array, array.length-1);
            System.arraycopy(array, 0, newArray, 0, index);
            System.arraycopy(array, index+1, newArray, index, array.length - index -1);
            array = newArray;
        }finally {
            lock.unlock();
        }
    }

    public E get(int index){
        return (E) array[index];
    }

    public static void main(String[] args) {
        CustomCopyOnWriteArrayList<Integer> list = new CustomCopyOnWriteArrayList<>();


        list.add(1);

        for(int i=0;i< list.size();i++){
            System.out.println(list.get(i));
            list.add(2);
            if(list.size() > 5) {
                break;
            }
        }

        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(1);
        for(Integer val: arrayList){
            System.out.println(val);
            arrayList.add(2);
            if(arrayList.size() > 5) {
                break;
            }
        }

    }



}
