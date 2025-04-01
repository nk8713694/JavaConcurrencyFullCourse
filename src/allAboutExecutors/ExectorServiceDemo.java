package allAboutExecutors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExectorServiceDemo {
    public static void main(String[] args) {

//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//
//        for(int i=0;i<20;i++){
//            final int finalI = i;
//            executorService.submit(
//                    ()->{
//                        System.out.println("Task submitted "+finalI+" "+Thread.currentThread().getName());
//                    }
//            );
//        }

        ExecutorService executorServiceCache = Executors.newCachedThreadPool();

        for(int i=0;i<100;i++){
            final int finalI = i;
            executorServiceCache.submit(
                    ()->{
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Cache Task submitted "+finalI+" "+Thread.currentThread().getName());
                    }
            );
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> submit = executorService.submit(() -> {
            return "Hello";
        });

        System.out.println("Main thread");

        try {
            System.out.println(submit.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();
        executorServiceCache.shutdown();

        executorService.shutdownNow();
    }
}
