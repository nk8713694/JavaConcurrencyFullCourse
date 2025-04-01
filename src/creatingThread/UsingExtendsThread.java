package creatingThread;


public class UsingExtendsThread extends Thread {

    @Override
    public void run() {
        System.out.println("Thread is running "+Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        System.out.println("Main Thread is running "+Thread.currentThread().getName());

        UsingExtendsThread usingExtendsThread = new UsingExtendsThread();

        usingExtendsThread.start();
    }

}
