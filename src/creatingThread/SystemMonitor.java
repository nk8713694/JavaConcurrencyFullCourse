package creatingThread;

public class SystemMonitor extends Thread {
    boolean running = true;

    @Override
    public void run() {
        while (running) {
            System.out.println("Monitoring System Resources ..");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    @Override
    public void start() {
        System.out.println("Starting Monitoring "+this.getName());
        super.start();
    }

    public void shutdown(){
        running = false;
        this.interrupt();
        System.out.println(this.getName()+"  Shutting down");
    }

    public static void main(String[] args) {
        SystemMonitor systemMonitor = new SystemMonitor();
        systemMonitor.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        systemMonitor.shutdown();
    }
}
