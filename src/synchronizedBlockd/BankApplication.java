package synchronizedBlockd;

public class BankApplication {

    public double amount;

    public BankApplication(double amount) {
        this.amount = amount;
    }

    public synchronized void depositAmount(double addAmount){
        amount+= addAmount;
    }

    public void withdraw(double withDrawAmount){
        if(withDrawAmount > amount  || amount ==0) {
            throw new IllegalArgumentException("exce");
        }
        synchronized (this) {
            amount-=withDrawAmount;
        }
    }

    public double getAmount() {
        return amount;
    }
}

class TransferService {

    public void transfer(BankApplication from, BankApplication to, double amount) {
        synchronized (from){
            synchronized (to){
                from.withdraw(amount);
                to.depositAmount(amount);
            }
        }
    }

    public static void main(String[] args) {
        TransferService transferService = new TransferService();

        BankApplication from = new BankApplication(1000);
        BankApplication to = new BankApplication(1000);
        transferService.transfer(from, to, 500 );

        System.out.println(from.getAmount());
        System.out.println(to.getAmount());
    }
}
