package src.main.java.single_responsibility.withSRP;

class UserValidator {
    public boolean validate(User user) {
        return user.getEmail().contains("@") && user.getUsername().length() > 0;
    }
}
