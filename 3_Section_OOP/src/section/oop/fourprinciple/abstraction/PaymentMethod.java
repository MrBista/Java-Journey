package src.section.oop.fourprinciple.abstraction;

import java.util.UUID;

public abstract class PaymentMethod {

    protected String transactionId;

    // shared behavior, enaknya di abstraction ini juga sih
    public void generateTransactionId() {
        this.transactionId = UUID.randomUUID().toString();
    }

    public abstract void paymentProcess();
    public abstract boolean sendReciept();


}
