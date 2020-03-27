package ph.devcon.rapidpass.service;

import ph.devcon.rapidpass.model.RapidPassRequest;

/**
 * The {@link PwaService} interface provides the business logic for servicing PWA requests.
 */
public interface PwaService {

    /**
     * Creates a new request for a RapidPass.
     *
     * @param rapidPassRequest request data
     */
    void createPassRequest(RapidPassRequest rapidPassRequest);

    /**
     * Gets a RapidPass request based on the passed in idNum.
     *
     * @param referenceId mobileNum or plateNum
     * @return Matching {@link RapidPassRequest} data or null, if not found.
     */
    RapidPassRequest getPassRequest(String referenceId);
}
