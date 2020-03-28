package ph.devcon.rapidpass.services.notifications;

import java.lang.Exception;

public class NotificationException extends Exception {

    public NotificationException(Exception e) {
        super(e);
    }

    public NotificationException(String message) {
        super(message);
    }

}