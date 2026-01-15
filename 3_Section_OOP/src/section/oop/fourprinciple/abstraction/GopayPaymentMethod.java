package src.section.oop.fourprinciple.abstraction;

public class GopayPaymentMethod extends PaymentMethod{
    private String phoneNumber;
    @Override
    public void paymentProcess() {
        System.out.println("Detail impelemntation goes here and we can also add some logic here");
    }

    @Override
    public boolean sendReciept() {
        System.out.println("Send to receipt with phone number : " +this.phoneNumber + " with transaction id : " + super.transactionId );
        return false;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber= phoneNumber;
    }
}
