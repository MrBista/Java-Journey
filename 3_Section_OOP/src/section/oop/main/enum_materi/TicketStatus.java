package src.section.oop.main.enum_materi;

public enum TicketStatus {
    BARU("Ticket yang baru dibuat", 0),
    PROSES("Ticket yang sudah ditangani petugas", 1),
    SETUJUI("Ticket yang sudah disetujui petugas", 2);

    private final String desc;
    private final int code;

    private TicketStatus(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }


    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public static TicketStatus fromCode(int code) {
        for (TicketStatus status : TicketStatus.values()) {
            if (status.code == code) {
                return status;
            }

        }
        throw new IllegalArgumentException("Status code " + code + " not found");
    }


}
