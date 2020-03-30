package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static ph.devcon.rapidpass.enums.AccessPassStatus.PENDING;

/**
 * Data model representing a request to create a new {@link AccessPass}. It contains details about an AccessPass and a {@link Registrant}.
 * <p>
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 * <p>
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

    // required fields reference: https://docs.google.com/spreadsheets/d/1YemwomlhoKnDcBDI3OlrA5-yMSOSfA3BHj0N6PijmT4/edit#gid=0
    @NotNull
    private PassType passType;

    @NotEmpty
    private String aporType;
    @NotEmpty
    private String firstName;
    private String middleName;
    @NotEmpty
    private String lastName;
    private String suffix;
    @NotEmpty
    private String company;
    @NotEmpty
    private String idType;
    @NotEmpty
    private String identifierNumber;
    @NotEmpty
    private String mobileNumber; // todo validate. use validation annotations.
    @NotEmpty
    private String email;  // todo validate. use validation annotations.
    @NotEmpty
    private String originName;
    @NotEmpty
    private String originStreet;
    @NotEmpty
    private String originCity;
    @NotEmpty
    private String destName;
    @NotEmpty
    private String destStreet;
    @NotEmpty
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
    private AccessPassStatus accessPassStatus = PENDING;

    public String getName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }


}
