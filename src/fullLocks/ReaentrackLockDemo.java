package fullLocks;

import java.util.concurrent.locks.ReentrantLock;

public class ReaentrackLockDemo {

    ReentrantLock reentrantLock = new ReentrantLock();


    public void outerMethod(){
        reentrantLock.lock();
        try {
            System.out.println("POuter Methids");
            Thread.sleep(1000);
            innerMethod();
        }catch (InterruptedException e){

        } finally {
            reentrantLock.unlock();
        }

    }

    public void innerMethod() {
        reentrantLock.lock();
        try {
            System.out.println("Inner Methids");
            Thread.sleep(1000);

        }catch (InterruptedException e){

        } finally {
            reentrantLock.unlock();
        }
    }


    public static void main(String[] args) {
        ReaentrackLockDemo reaentrackLockDemo = new ReaentrackLockDemo();
        reaentrackLockDemo.outerMethod();

    }

}
