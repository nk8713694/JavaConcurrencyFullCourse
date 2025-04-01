package futuresAndCallbacks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExmple {

    public static void main(String[] args) {
        System.out.println("Main");
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                ()->{
                    System.out.println("Starting");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Execute Thread Async");
                }
        );
// Perform other
        try {
            System.out.println("Other Stuff");
            completableFuture.get();
            System.out.println("Completed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }
}
