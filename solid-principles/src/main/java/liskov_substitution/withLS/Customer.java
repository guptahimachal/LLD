package src.main.java.liskov_substitution.withLS;

public class Customer extends User implements Rewardable {

    @Override
    public Integer getScore() {
        return 0;
    }

}
