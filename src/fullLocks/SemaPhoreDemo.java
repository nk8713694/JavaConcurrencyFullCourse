package fullLocks;

import java.util.concurrent.Semaphore;

public class SemaPhoreDemo {

    final Semaphore semaphore ;

    public SemaPhoreDemo(int numberofthreads) {
        this.semaphore = new Semaphore(numberofthreads);
    }

    public void printer()  {

        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+ " Printing... ");
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            System.out.println("Released ");
            semaphore.release();
        }

    }

    public static void main(String[] args) {
        SemaPhoreDemo semaPhoreDemo = new SemaPhoreDemo(3);

        for(int i=0;i<10;i++){
            Thread thread = new Thread(semaPhoreDemo::printer);
            thread.start();
        }

    }
}
