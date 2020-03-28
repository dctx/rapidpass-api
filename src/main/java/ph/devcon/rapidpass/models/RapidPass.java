package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.Registrant;

import java.util.Date;

/**
 * This is the data definition for messaging to and from API consumers.
 *
 * For creating a RapidPass, please see {@link RapidPassRequest}.
 */
@Data
@Builder
public class RapidPass {

    private String referenceId;

    private String passType;

    private String aporType;

    private String controlCode;

    private String idType;

    private String identifierNumber;

    private String name;

    private String company;

    private String remarks;

    private Integer scope;

    private String limitations;

    private String originName;

    private String originStreet;

    private String originProvince;

    private String originCity;

    private String destinationName;

    private String destinationStreet;

    private String destinationCity;

    private String destinationProvince;

    private Date validFrom;

    private Date validTo;

    private String issuedBy;

    private String updates;

    private String status;

    private Date dateTimeCreated;

    private Date dateTimeUpdated;

    private Registrant registrantId;

    public static RapidPass buildFrom(AccessPass accessPass) {
        return RapidPass.builder()
                .referenceId(accessPass.getReferenceId())
                .controlCode(accessPass.getControlCode() == null? "" : accessPass.getControlCode())
                .passType(accessPass.getPassType())
                .aporType(accessPass.getAporType())
                .idType(accessPass.getIdType())
                .identifierNumber(accessPass.getIdentifierNumber())
                .name(accessPass.getName())
                .status(accessPass.getStatus())
                .validFrom(accessPass.getValidFrom())
                .validTo(accessPass.getValidTo())
                .destinationCity(accessPass.getDestinationCity())
                .destinationName(accessPass.getDestinationName())
                .destinationProvince(accessPass.getDestinationProvince())
                .originCity(accessPass.getOriginCity())
                .originName(accessPass.getOriginName())
                .originProvince(accessPass.getOriginProvince())
                .originStreet(accessPass.getOriginStreet())
                .dateTimeCreated(accessPass.getDateTimeCreated())
                .dateTimeUpdated(accessPass.getDateTimeUpdated())
                .registrantId(accessPass.getRegistrantId())
                .limitations(accessPass.getLimitations())
                .scope(accessPass.getScope())
                .remarks(accessPass.getRemarks())
                .company(accessPass.getCompany())
                .build();
    }
}
