package ph.devcon.rapidpass.service.notification;

public interface NotificationService {

    void send(NotificationMessage message) throws NotificationException;

}