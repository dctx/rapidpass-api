package ph.devcon.rapidpass.services.sms;

public class SMSPayload {
    private final String number;
    private final String message;

    /**
     * Set this to true once the correct SMS sender name has been configured on Semaphore.
     *
     * e.g. RapidPass or RapidPass.ph
     */
    private boolean isSenderEnabled = false;

    public SMSPayload(String number, String message) {
        this.number = number;
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        /**
         * Define the constant sender name configured in Semaphore.
         */
        return "RapidPass";
    }

    public boolean isSenderEnabled() {
        return isSenderEnabled;
    }
}
