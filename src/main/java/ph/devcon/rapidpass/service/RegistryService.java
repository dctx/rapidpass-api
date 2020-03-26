package ph.devcon.rapidpass.service;

import ph.devcon.rapidpass.model.RapidPassRequest;

import javax.annotation.Nullable;

/**
 * The {@link RegistryService} interface provides the business logic for servicing PWA requests.
 */
public interface RegistryService {

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
    @Nullable
    RapidPassRequest getPassRequest(String referenceId);
}
