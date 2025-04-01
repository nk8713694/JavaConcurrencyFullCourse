package threadLifeCycle;

public class ThreadLifeCycle implements Runnable {
    Object lock = new Object();

    public static void main(String[] args) {

        // New State
        Thread thread = new Thread(() -> {
            System.out.println("Thread is running");
        });
        // Runnable State
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        synchronized (lock){
            try {
                 // Wait State and Time Boounded Waiting State
                System.out.println("Thread began processing and going into waiting state");
                lock.wait(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
