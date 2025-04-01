package deadlocks;

import java.util.concurrent.locks.ReentrantLock;

public class StarVationFairLockExample {


    public static final ReentrantLock lock = new ReentrantLock();

    public static void print(String message){
        try{
            lock.lock();
            System.out.println(message);
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(() ->{
            print("This is thread 1");
        });
        Thread thread2 = new Thread(() ->{
            print("This is thread 2");
        });
        Thread thread3 = new Thread(() ->{
            print("This is thread 3");
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
