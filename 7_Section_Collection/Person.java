import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Person {
    private String name;
    private List<String> hobbies = new ArrayList<>();


    public Person() {
    }

    public void addHobie(String hobbie) {
        hobbies.add(hobbie);
    }

    // ini bahaya, karena dia tidak imutable maka bisa nge add dari sini
//    public List<String> getHobbies() {
//        return this.hobbies;
//    }

    public List<String> getHobbies() {
        return Collections.unmodifiableList(this.hobbies);
    }

    public void setName(String name) {
        this.name = name;
    }
}
