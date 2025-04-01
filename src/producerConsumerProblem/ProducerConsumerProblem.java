package producerConsumerProblem;

class Buffer {
    private int[] buffer;
    private int in;
    private int out;

    private int index;
    public Buffer(int size){
        this.buffer = new int[size];
        index = 0;
    }

    public synchronized void put(int data) {
        while (index == buffer.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        buffer[in] = data;
        in = (in+1)%buffer.length;
        index++;
        notify();
    }

    public synchronized int get() {
        while (buffer.length == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int data = buffer[out];
        out = (out+1)%buffer.length;
        index--;
        notify();
        return data;
    }
}

class Producer implements Runnable {
    private Buffer buffer;

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }


    @Override
    public void run() {
        while(true) {
            int data = (int) ( Math.random()*1000);
            buffer.put(data);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Consumer implements Runnable{
    private Buffer buffer;

    public Consumer(Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run() {
       while (true) {
           int data = buffer.get();
           System.out.println("Consumed Data " + data);
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
       }
    }
}



public class ProducerConsumerProblem {

    public static void main(String[] args) {
        Buffer buffer = new Buffer(10);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}
