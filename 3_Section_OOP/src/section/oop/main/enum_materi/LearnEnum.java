package src.section.oop.main.enum_materi;

public class LearnEnum {
    public static void main(String[] args) {
        int statusDone = OrderStatus.DONE.getCode();
        System.out.println("Status Done - " + statusDone);

        OrderStatus[] statusOrders = OrderStatus.values();
        for (OrderStatus statusOrder : statusOrders) {
            System.out.println("Staus order - " + statusOrder.name());
        }

        OrderStatus inProgressStatus = OrderStatus.valueOf("INPROGRES");
        System.out.println("Status in progress - " + inProgressStatus.name());


        TicketStatus statusBaru = TicketStatus.fromCode(1);
        System.out.println(statusBaru.name());

//        TicketStatus statusNotFound = TicketStatus.fromCode(100);


    }
}
