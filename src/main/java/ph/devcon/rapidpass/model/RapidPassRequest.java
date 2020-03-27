package ph.devcon.rapidpass.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static ph.devcon.rapidpass.model.RapidPassRequest.RequestStatus.PENDING;

/**
 * The {@link RapidPassRequest} class models a Rapid Pass request to either create or retrieve a rapid pass request.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RapidPassRequest {
    /**
     * Backend only reference number.
     */
    @NotNull
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private String refNum = UUID.randomUUID().toString();

    @NotNull
    private RapidPassRequest.PassType passType;
    private AccessType accessType;
    private String firstName;
    private String lastName;
    private String company;
    private String idType;
    private String plateOrId; // todo validate. use validation annotations.
    private String mobileNumber; // todo validate. use validation annotations.
    private String email;  // todo validate. use validation annotations.
    private String originAddress;
    private String destAddress;
    private String remarks;

    /**
     * The status of this request. Initially set to PENDING when built by builders.
     */
    @NotNull
    @Builder.Default
    private RequestStatus requestStatus = PENDING;

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

    /**
     * Types of Requests supported by RapidPass.
     */
    public enum PassType {
        INDIVIDUAL, VEHICLE
    }

    /**
     * Types of access supported to Rapid Pass. Ties to the agencies responsible for approving access.
     */
    public enum AccessType {
        // TODO validate and make sure we have the right types!
        // based from https://www.figma.com/file/jWgRtRX2FgOcfif5PBGxeI/RapidPass?node-id=453%3A310 for now

        /**
         * Medical
         */
        MED,
        /**
         * Basic Service
         */
        BS,
        /**
         * Business Process Outsourcing
         */
        BPO,
        /**
         * Agriculture
         */
        A,
        /**
         * Logistics
         */
        L,
        /**
         * Deliverables
         */
        D,
        /**
         * Others
         */
        O
    }

}
