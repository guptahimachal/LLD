package src.main.java.dependency_inversion.withDI;

public class Database implements UserRepository {

    @Override
    public void saveData(String data) {
        System.out.println("Saving to db " + data);
    }
}
