package visbility;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    AtomicInteger counter;

    volatile int counterV = 0;

    public Counter(int counter) {
        this.counter = new AtomicInteger(counter);
        this.counterV = counter;
    }

    public void incrementCounter(){
        counter.getAndIncrement();
        counterV++;
    }

    public int getCounter() {
        return counter.get();
    }

    public int getCounterV() {
        return counterV;
    }



    public static void main(String[] args) {
        Counter counter = new Counter(0);

        Thread thread1 = new Thread(
                ()-> {
                    for(int i=0;i<1000;i++){
                        counter.incrementCounter();
                    }
                }
        );
        Thread thread2 = new Thread(
                ()-> {
                    for(int i=0;i<1000;i++){
                        counter.incrementCounter();
                    }
                }
        );

        thread1.start();
        thread2.start();


        try{
            thread1.join();
            thread2.join();
        }catch (InterruptedException e){

        }

        System.out.println(counter.getCounter());
        System.out.println(counter.getCounterV());

    }
}
