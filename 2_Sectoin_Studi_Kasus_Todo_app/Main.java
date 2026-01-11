import java.util.Scanner;

public class Main {
    static String[]todos = new String[10];

    static Scanner inputUser = new Scanner(System.in);

    private static String NAME = "Mr Bista";

    public static void main(String[] args) {
//        System.out.print("Tambah todo : ");
//        String valueOfInput = inputUser.next();
//        System.out.println(valueOfInput);

        vieMainFunction();
    }


    public static void vieMainFunction() {
        while(true) {
            System.out.println("Hallo Selamat datang " + NAME);
            System.out.println("1. Lihat todo");
            System.out.println("2. Tambah Todo");
            System.out.println("3. Hapus Todo");
            System.out.println("4. Keluar(x)");
            System.out.print("Pilih: ");
            String valueInput  = inputUser.nextLine();


            if (valueInput.equals("4") || valueInput.equals("x")) {
                break;
            }else if (valueInput.equals("1")) {
                viewTodoList();
            }else if (valueInput.equals("2")) {
                System.out.print("Masuk Todo Baru: ");
                String todoInput = inputUser.nextLine();
                addTodoList(todoInput);
            }else if (valueInput.equals("3")) {
                System.out.print("Masukan Id Todo yang ingin dihapus: ");
                int removeInput = inputUser.nextInt();
                inputUser.nextLine();
                removeTodoList(removeInput);
            }

        }
    }


    public static void viewTodoList() {
        System.out.println("TODO FOR " + NAME + ": ");
        int num = 0;
        for (int i = 0; i < todos.length; i ++ ) {
            if (todos[i] != null) {
                num ++;
                System.out.println(num + ". " + todos[i]);
            }

        }

    }

    public static void addTodoList(String todo) {
        // 1. cek dulu apakah masih ada space
        // 2. kalau ga ada maka throw suruh selesaiin dulu todo yang udah ada
        // 3. kalau masih ada todo spacenya maka tambah aja

        boolean isSuccessAdding = false;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i] == null) {
                todos[i] = todo;
                isSuccessAdding = true;
                break;
            }
        }

        if (isSuccessAdding) {
            System.out.println("Berhasil menambahkan todo baru berupa : " + todo);
        }else {
            System.out.println("Tolong Selesain Dulu todo yang udah ada ya bre");
        }


    }

    public static void removeTodoList(int id) {

        boolean isSuccessDeleteTodo = false;

        int num = 0;
        for (int i = 0; i < todos.length; i++) {
            if (todos[i] != null) {
                num ++;
                if (id == num) {
                    todos[i] = null;
                    isSuccessDeleteTodo = true;
                }

            }
        }

        if (!isSuccessDeleteTodo) {
            System.out.println("Todo dengan id " + id + " tidak ditemukan");
        } else {
            System.out.println("Anda berhasil menghpuas todo dengan id " + id);
        }


    }



}
