package creatingThread;

public class UsingImplementsRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Start thread in Runnable "+Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        UsingImplementsRunnable usingImplementsRunnable = new UsingImplementsRunnable();
        usingImplementsRunnable.run();

        Thread thread = new Thread(usingImplementsRunnable);
        thread.start();
    }
}
