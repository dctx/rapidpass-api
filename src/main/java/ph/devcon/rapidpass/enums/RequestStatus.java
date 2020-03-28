package ph.devcon.rapidpass.enums;


/**
 * The Statuses that a RapidPass Request can have.
 */
public enum RequestStatus {
    /**
     * Pending request. This is the initial state.
     */
    PENDING,
    /**
     * Approved request.
     */
    APPROVED,
    /**
     * Denied request.
     */
    DENIED
}