package src.main.java.interface_segregation.withoutIs;

public class InternalNotificationTransformer implements NotificationTransformer {
    @Override
    public Boolean validate(String notification) {
        // No validations need to be performed, Here we can improve this
        return null;
    }

    @Override
    public String transform(String notification) {
        return "";
    }
}
