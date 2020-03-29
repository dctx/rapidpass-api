package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.enums.APORType;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static ph.devcon.rapidpass.enums.RequestStatus.PENDING;

/**
 * Data model representing a request to create a new {@link AccessPass}. It contains details about an AccessPass and a {@link Registrant}.
 *
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 *
 * API consumers send {@link RapidPassRequest} when they send a query for creating a {@link AccessPass} (POST).
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
    private String identifierNumber;
    private String mobileNumber; // todo validate. use validation annotations.
    private String email;  // todo validate. use validation annotations.
    private String originName;
    private String originStreet;
    private String originCity;
    private String destName;
    private String destStreet;
    private String destCity;
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
