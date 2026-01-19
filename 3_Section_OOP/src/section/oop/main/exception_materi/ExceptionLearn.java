package src.section.oop.main.exception_materi;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExceptionLearn {
    public static void main(String[] args) {
        /*
            ini akan di cek saat runtime,
            kalau misal code nya ga memenuhi kondisi atau masuk ke dalam exception yang berupa exception runtime dia akan
         */
        validUmur(2);

//        try {
//            validUmur(2);
//
//        }catch (IllegalArgumentException e) {
//            System.out.println("Error has occured: " + e.getMessage());
//        }

//        try {
//            System.out.println("Running from: " + System.getProperty("user.dir"));
//
//
//            File fileMateri = new File("materi.md");
//            bacaFile(fileMateri);
//
//        }catch (IOException e) {
//            System.out.println("Error has occured " + e.getMessage());
//        }
    }


    // throws ini biasanya ketika ada checked exception yang ga mau kita handle langsung di methodnya
    static void bacaFile(String namaFile) throws IOException {
        FileReader fr = new FileReader(namaFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        System.out.println("Isi file: " + line);

        br.close();
    }
    static void bacaFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        System.out.println("Isi file: " + line);

        br.close();
    }

    static void validUmur(int age){
        if (age < 18) {
            throw new IllegalArgumentException("Mohon maaf umur belum cukup kids");
        }

        System.out.println("Hai Anda mencukupi umur");
    }
}

