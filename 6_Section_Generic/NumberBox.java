
// ini nanti dia akan hanya boleh dipakai oleh tipe number atau sublcass dari number(upper bound contravariant)
public class NumberBox<T extends Number> {
    private T number;

    public NumberBox() {
    }

    public NumberBox(T num) {
        this.number = num;
    }


    public T getNumber() {
        return this.number;
    }

    public void setNumber(T num) {
        this.number = num;
    }
}

