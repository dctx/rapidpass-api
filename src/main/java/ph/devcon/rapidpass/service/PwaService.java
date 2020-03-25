package ph.devcon.rapidpass.service;

import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.model.RapidPassRequest.RequestType;

import javax.annotation.Nullable;

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
     * @param idNum       mobileNum or plateNum
     * @param requestType type of the request to get
     * @return Matching {@link RapidPassRequest} data or null, if not found.
     */
    @Nullable
    RapidPassRequest getPassRequest(String idNum, RequestType requestType);
}
