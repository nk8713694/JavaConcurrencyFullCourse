package deadlocks;

public class DeadlockExample {

    private static Object lock1 = new Object();
    private static Object lock2 = new Object();


    private static  Thread thred1 = new Thread(()->{
        synchronized (lock1){
            System.out.println("Acquiring Resource 1 by "+Thread.currentThread().getName());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (lock2) {
                System.out.println("Acquiring Resource 2 "+Thread.currentThread().getName());
            }
        }
    });

    private static  Thread thred2 = new Thread(()->{
        synchronized (lock1){
            System.out.println("Acquiring Resource 2 by "+Thread.currentThread().getName());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (lock2) {
                System.out.println("Acquiring Resource 1 "+Thread.currentThread().getName());
            }
        }
    });

    public static void main(String[] args) {
        thred1.start();
        thred2.start();
    }
}
