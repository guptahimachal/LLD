package src.main.java.interface_segregation.withIs;

public class ClientNotificationTransformer implements ValidateInterface, TransformerInterface {

    @Override
    public String transform(String notification) {
        return "";
    }

    @Override
    public Boolean validate(String notification) {
        return null;
    }
}
