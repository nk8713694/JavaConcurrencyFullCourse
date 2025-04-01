package fullLocks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaWriteLockDemo {

    final ReadWriteLock lock = new ReentrantReadWriteLock();

    int score = 0;


    void updateScore(int newScore){
        lock.writeLock().lock();
        try {
            System.out.println("Writing Block");
            score += newScore;
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
            System.out.println("Writing Unblock");
        }
    }

    int getScore(){
        System.out.println("Reading checks for wrting");
        lock.readLock().lock();
        try {
            System.out.println("Reading ");
            Thread.sleep(1000);
            return score;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
            System.out.println("Reading unlock");
        }
    }

    public static void main(String[] args) {
        ReaWriteLockDemo demo = new ReaWriteLockDemo();

        Thread thread = new Thread(()->{
            for(int i=0;i<1000;i++)
             demo.updateScore(i);
        });

        Thread thread3 = new Thread(()->{
            for(int i=0;i<1000;i++)
                System.out.println(demo.getScore());
        });


        Thread thread2 = new Thread(()->{
            for(int i=0;i<1000;i++)
                demo.updateScore(i);
        });

        thread.start();
        thread3.start();
        thread2.start();


        try {
            thread.join();
            thread2.join();
            thread3.join();
        }catch (InterruptedException e){

        }

    }

}
