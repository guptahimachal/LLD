package src.main.java.interface_segregation.withoutIs;

import java.util.Objects;

public class ClientNotificationTransformer implements NotificationTransformer {

    @Override
    public Boolean validate(String notification) {
        return Objects.nonNull(notification) && !notification.isEmpty();
    }

    @Override
    public String transform(String notification) {
        return "";
    }
}
