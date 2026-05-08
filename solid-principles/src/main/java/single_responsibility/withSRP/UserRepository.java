package src.main.java.single_responsibility.withSRP;

class UserRepository {
    public void save(User user) {
        // Simulate saving to database
        System.out.println("Saving user to database: " + user.getUsername());
    }
}
