package src.main.java.interface_segregation.withoutIs;

public interface NotificationTransformer {

    Boolean validate(String notification);

    String transform(String notification);

}
