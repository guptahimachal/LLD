package src.main.java.liskov_substitution.withoutLS;

public class Customer extends User {

    @Override
    Integer getScore() {
        return 0;
    }

    @Override
    String getReport() {
        throw new RuntimeException("User has no access to generate report");
    }

}
