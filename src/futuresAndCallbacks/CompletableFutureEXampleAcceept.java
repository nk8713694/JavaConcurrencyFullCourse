package futuresAndCallbacks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureEXampleAcceept {
    public static void main(String[] args) {

        CompletableFuture<String> futureJob = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Hello";
                }
        );

        CompletableFuture<String> appliedFuture = futureJob.thenApply(
                name -> name + "Wordld"
        );

        appliedFuture.thenAccept(
                (message)-> System.out.println("printing -> "+ message)
        );

        try {
            appliedFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


    }
}
