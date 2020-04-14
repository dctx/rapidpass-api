package ph.devcon.rapidpass.validators;

/**
 * Class for displaying a readable validation exception as its default message.
 */
public class ReadableValidationException extends IllegalArgumentException {
    public ReadableValidationException(String s) {
        super(s);
    }
}
