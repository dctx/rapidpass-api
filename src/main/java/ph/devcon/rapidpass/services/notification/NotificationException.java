package ph.devcon.rapidpass.services.notification;

import java.lang.Exception;

public class NotificationException extends Exception {

    public NotificationException(Exception e) {
        super(e);
    }

}