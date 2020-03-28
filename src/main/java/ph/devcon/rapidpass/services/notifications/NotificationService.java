package ph.devcon.rapidpass.services.notifications;

public interface NotificationService {

    void send(NotificationMessage message) throws NotificationException;

}