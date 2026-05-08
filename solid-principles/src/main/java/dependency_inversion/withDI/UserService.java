package src.main.java.dependency_inversion.withDI;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(String userName) {
        userRepository.saveData(userName);
    }


}
