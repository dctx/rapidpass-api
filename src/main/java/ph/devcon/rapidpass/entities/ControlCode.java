package ph.devcon.rapidpass.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/*
 * Schema definition for the response of the control codes, on the OpenAPI.yaml.
 *
 * @see {https://gitlab.com/dctx/rapidpass/rapidpass-api/-/blob/develop/src/main/resources/rapidpass-openapi.yaml}
 */
public class ControlCode {
    private String passType;
    /**
     * Directly maps to {@link ph.devcon.rapidpass.enums.APORType} of an {@link AccessPass}, such as MED, GOV, etc.
     */
    private String accessType;
    private String name;
    private String company;
    private String idType;
    /**
     * TODO: This needs to be replaced into two different fields later on, as recommended by Roy.
     */
    private String plateOrId;
    private String mobileNumber;
    private String email;
    private String originAddress;
    private String destAddress;
    private String remarks;
    private String controlCode;
    private String status;

    /**
     * Assuming that this is ISO8601.
     *
     * Not sure how to use the date utilities class that we have.
     */
    private String validUntil;

    public static ControlCode buildFrom(AccessPass accessPass) {

        // TODO: Transform integer control code into string encoding
        String encodedControlCode = String.valueOf(accessPass.getControlCode());

        return ControlCode.builder()
            .passType(accessPass.getPassType())
            .accessType(accessPass.getAporType().toString())
                .name(accessPass.getName())
                .company(accessPass.getCompany())
                .idType(accessPass.getIdType())
                .plateOrId(accessPass.getPlateOrId())
                // Cannot retrieve registrant data from access path. Or at least, it's a separate call altogether
                .originAddress(accessPass.getOriginAddress())
                .destAddress(accessPass.getDestinationAddress())
                .remarks(accessPass.getRemarks())
                .controlCode(encodedControlCode)
                .status(accessPass.getStatus())
                .build();
    }
}
