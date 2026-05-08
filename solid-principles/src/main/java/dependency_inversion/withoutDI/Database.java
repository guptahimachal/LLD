package src.main.java.dependency_inversion.withoutDI;

public class Database {

    public void save(String data) {
        System.out.println("Saving to db " + data);
    }

}
