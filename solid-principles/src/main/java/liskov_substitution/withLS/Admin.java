package src.main.java.liskov_substitution.withLS;

public class Admin extends User implements Reportable {
    @Override
    public String getReport() {
        return "";
    }
}
