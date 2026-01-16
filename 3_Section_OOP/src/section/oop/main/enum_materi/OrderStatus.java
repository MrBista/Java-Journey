package src.section.oop.main.enum_materi;

public enum OrderStatus {
    PENDING( 1),
    INPROGRES(2),
    DONE(3);

    private final int code;

    private OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
