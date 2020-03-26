package ph.devcon.rapidpass.services.notification;

public interface NotificationService {

    void send(NotificationMessage message) throws NotificationException;

}