package synchronizedBlockd;

public class Counter {

    int counter;

    public Counter(int counter) {
        this.counter = counter;
    }

    public void incrementCounter(){
        // Big Code
        synchronized (this){
            counter++;
        }

        // Big Code
    }

    public int getCounter(){
        return counter;
    }

    public static void main(String[] args) {
        Counter counter1 = new Counter(0);
        Thread thread1 = new Thread(()->{
            for(int i=0;i<1000;i++){
                counter1.incrementCounter();
            }
        });

        Thread thread2 = new Thread(()->{
            for(int i=0;i<1000;i++){
                counter1.incrementCounter();
            }
        });

        Counter counter2 = new Counter(0);
        Thread thread3 = new Thread(()->{
            for(int i=0;i<1000;i++){
                counter2.incrementCounter();
            }
        });

        Thread thread4 = new Thread(()->{
            for(int i=0;i<1000;i++){
                counter2.incrementCounter();
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        }catch (InterruptedException e){

        }

        System.out.println(counter1.getCounter());
        System.out.println(counter2.getCounter());
    }
}


class Singleton {

    static Singleton singleton;

    public Singleton() {
    }

    public static Singleton getInstance(){
       if(singleton == null) {
           synchronized (Singleton.class) {
               if(singleton == null)
                singleton = new Singleton();
           }
       }
       return singleton;
    }
}