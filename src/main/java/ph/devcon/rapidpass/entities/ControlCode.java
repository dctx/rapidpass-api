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
    private String referenceId;

    private String controlCode;

    private String passType;

    public static ControlCode buildFrom(AccessPass accessPass) {

        // TODO: Transform integer control code into string encoding
        String encodedControlCode = String.valueOf(accessPass.getControlCode());

        return ControlCode.builder()
            .passType(accessPass.getPassType())
            .referenceId(accessPass.getReferenceID())
            .controlCode(encodedControlCode)
            .build();
    }
}
