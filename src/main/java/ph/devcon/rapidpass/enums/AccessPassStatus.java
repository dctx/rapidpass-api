package ph.devcon.rapidpass.enums;


/**
 * The Statuses that a RapidPass Request can have.
 */
public enum AccessPassStatus {
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
    DECLINED,
    /**
     * Access pass is no longer valid
     */
    SUSPENDED;

    public static Boolean isValid(String status) {
      for (AccessPassStatus s: AccessPassStatus.values()) {
          if (s.name().equals(status.toUpperCase())) {
              return true;
          }
      }
      return false;
    }
}