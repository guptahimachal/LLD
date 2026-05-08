package src.main.java.dependency_inversion.withoutDI;

public class UserService {

    private Database database = new Database();

    public void saveUser(String userName) {
        database.save(userName);
    }


}
