package src.main.java.single_responsibility.withoutSRP;

public class User {

    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // Method to validate user data
    public boolean validate() {
        return email.contains("@") && username.length() > 0;
    }

    // Method to save user data to database
    public void save() {
        // Simulate saving to database
        System.out.println("Saving user to database: " + username);
    }
}
