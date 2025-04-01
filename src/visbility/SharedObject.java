package visbility;

public class SharedObject {

    private volatile boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public static void main(String[] args) {
        SharedObject object = new SharedObject();

        Thread thread1 = new Thread(
                () -> object.setFlag(true)
        );

        Thread thread3 = new Thread(
                () -> object.setFlag(false)
        );

        Thread thread2 = new Thread(
                () -> System.out.println(object.isFlag())
        );

        thread1.start();
        thread3.start();
        thread2.start();

    }
}
