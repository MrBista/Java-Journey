public class Box <T>{
    private T barang;

    public Box() {
    }

    public Box(T barang) {
        this.barang = barang;
    }

    public T getBarang() {
        return barang;
    }

    public void setBarang(T barang) {
        this.barang = barang;
    }
}
