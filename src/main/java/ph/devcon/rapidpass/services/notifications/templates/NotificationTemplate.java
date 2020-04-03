package ph.devcon.rapidpass.services.notifications.templates;

/**
 * Generates an object of type E, following the template needed for the specified implementation.
 *
 * Dependencies can be introduced via constructor, or builder pattern.
 * @param <E>
 */
public interface NotificationTemplate<E> {
    E compose();
}
