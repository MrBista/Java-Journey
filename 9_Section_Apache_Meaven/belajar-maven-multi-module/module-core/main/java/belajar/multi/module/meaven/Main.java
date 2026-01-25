package belajar.multi.module.meaven;

import belajar.multi.module.meaven.data.Person;
import com.google.gson.Gson;

/**
 * Hello world!
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("Hello World!");
        Person bismaPerson = new Person("Bisma", 20);
        Gson gson = new Gson();
        String bismaNameJson = gson.toJson(bismaPerson);
        System.out.println(bismaNameJson);


    }
}
