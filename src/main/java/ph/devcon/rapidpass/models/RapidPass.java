package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.utilities.DateOnlyFormat;

import java.util.Date;

/**
 * Data model representing an {@link AccessPass}, but is only a subset of the model's properties.
 *
 * API consumers send and receive {@link RapidPass} when interacting with the API for registering a rapid pass (GET, PUT).
 *
 * API consumers send {@link RapidPassRequest} when they send a query for creating a {@link AccessPass} (POST).
 *
 * This is JSON format returned to the user when they request for a GET on the AccessPass Resource.
 */
@Data
@Builder
public class RapidPass {

    private PassType passType;
    /**
     * Authorized Personnel Outside Residence
     */
    private String aporType;
    private String referenceId;
    private String controlCode;
    private String name;
    private String company;
    private String idType;
    private String identifierNumber;
    private String destName;
    private String destStreet;
    private String destCity;
    private String status;
    private String validFrom;
    private String validUntil;
    private String remarks;

    public static RapidPass buildFrom(AccessPass accessPass) {
        // TODO: If you want to return only a subset of properties from {@link AccessPass}, do so here.
        return RapidPass.builder()
                .referenceId(accessPass.getReferenceID())
                .controlCode(accessPass.getControlCode() == null? "" : accessPass.getControlCode())
                .passType(PassType.valueOf(accessPass.getPassType()))
                .aporType(accessPass.getAporType())
                .name(accessPass.getName())
                .company(accessPass.getCompany())
                .idType(accessPass.getIdType())
                .identifierNumber(accessPass.getIdentifierNumber())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom() == null ? "" : DateOnlyFormat.format(new Date(accessPass.getValidFrom().toEpochSecond())))
                .validUntil(accessPass.getValidTo() == null ? "" : DateOnlyFormat.format(new Date(accessPass.getValidTo().toEpochSecond())))
                .destName(accessPass.getDestinationName())
                .destStreet(accessPass.getDestinationStreet())
                .destCity(accessPass.getDestinationCity())
                .remarks(accessPass.getRemarks())
                .build();
    }
}
