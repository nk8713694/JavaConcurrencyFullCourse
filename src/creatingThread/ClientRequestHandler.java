package creatingThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRequestHandler implements Runnable{
    ClientRequest clientRequest;

    public ClientRequestHandler(ClientRequest clientRequest) {
        this.clientRequest = clientRequest;
    }

    @Override
    public void run() {
        System.out.println("Processing Request "+clientRequest.getId());
        try {
            Thread.sleep(5000);
            System.out.println("Process Complete "+clientRequest.getId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);
        for(int i=0;i<10;i++) {
            service.submit(new ClientRequestHandler(new ClientRequest(i)));
        }
        service.shutdown();
    }
}

class ClientRequest {
    private int id;

    public ClientRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
