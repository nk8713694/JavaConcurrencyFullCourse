package futuresAndCallbacks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> callable = () -> {
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
            return "Hello world";
        };

        Future<String> future =  executorService.submit(callable);

        try {
            String data = future.get();
            System.out.println(data);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();

    }
}
