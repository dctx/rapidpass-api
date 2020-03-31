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
    private String originProvince;
    @NotEmpty
    private String destName;
    @NotEmpty
    private String destStreet;
    @NotEmpty
    private String destCity;
    @NotEmpty
    private String destProvince;
    private String remarks;

    public String getName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    public static RapidPassRequest buildFrom(RapidPassCSVdata csvData) {
        return RapidPassRequest.builder()
                .passType(PassType.valueOf(csvData.getPassType()))
                .aporType(csvData.getAporType())
                .firstName(csvData.getFirstName())
                .middleName(csvData.getMiddleName())
                .lastName(csvData.getLastName())
                .suffix(csvData.getSuffix())
                .company(csvData.getCompany())
                .idType(csvData.getIdType())
                .identifierNumber(csvData.getIdentifierNumber())
                .mobileNumber(csvData.getMobileNumber())
                .email(csvData.getEmail())
                .originName(csvData.getOriginName())
                .originStreet(csvData.getOriginStreet())
                .originCity(csvData.getOriginCity())
                .destName(csvData.getDestName())
                .destStreet(csvData.getDestStreet())
                .destCity(csvData.getDestCity())
                .remarks(csvData.getRemarks())
                .build();
    }


}
