package ph.devcon.rapidpass.service.notification;

import java.lang.Exception;

public class NotificationException extends Exception {

    public NotificationException(Exception e) {
        super(e);
    }

    public NotificationException(String message) {
        super(message);
    }

}