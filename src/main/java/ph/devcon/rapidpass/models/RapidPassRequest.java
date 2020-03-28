package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static ph.devcon.rapidpass.enums.RequestStatus.PENDING;

/**
 * The {@link RapidPassRequest} class models a Rapid Pass Request.
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
    private PassType passType;
    private String aporType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String company;
    private String idType;
    private String plateOrId; // todo validate. use validation annotations.
    private String mobileNumber; // todo validate. use validation annotations.
    private String email;  // todo validate. use validation annotations.
    private String originAddress;
    private String destAddress;
    private String remarks;

    /**
     * Control number set when APPROVED.
     */
    private String controlCode;

    /**
     * The status of this request. Initially set to PENDING when built by builders.
     */
    @NotNull
    @Builder.Default
    private RequestStatus requestStatus = PENDING;

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    
}
