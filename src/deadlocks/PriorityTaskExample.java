package deadlocks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityTaskExample implements Runnable, Comparable<PriorityTaskExample> {
    private int priority;

    public PriorityTaskExample(int priority) {
        this.priority = priority;
    }

    @Override
    public void run() {
        System.out.println("The thread is running iwth priotity "+this.priority);
    }

    @Override
    public int compareTo(PriorityTaskExample o) {
        return Integer.compare(this.priority, o.priority);
    }

    public static void main(String[] args) {

        ExecutorService executorService = new ThreadPoolExecutor(
                2,
                2,
                1,
                TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>()
        );

        for(int i=0;i<10;i++){
            executorService.execute(new PriorityTaskExample(i));
        }

        executorService.shutdown();
    }

}
