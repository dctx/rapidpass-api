package ph.devcon.rapidpass.exceptions;

/**
 * @author czeideavanzado
 */
public class AccountLockedException extends Exception {

    public AccountLockedException(String message) {
        super(message);
    }
}
