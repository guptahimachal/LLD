package src.main.java.liskov_substitution.withoutLS;

public class Admin extends User {

    @Override
    Integer getScore() {
        return 0;
    }

    @Override
    String getReport() {
        return "";
    }
}
