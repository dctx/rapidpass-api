package ph.devcon.rapidpass.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * The {@link UpdateRapidPassRequest} class models the json for a request to update a Rapid Pass Request.
 *
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRapidPassRequest {

    private String referenceId;

    @NotNull
    private RapidPassRequest.PassType passType;

    private String plateOrId;

    /**
     * Must follow ISO8601.
     * Format: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
     *
     * e.g. "2020-04-01T12:30:00.016+08:00"
     */
    private String validUntil;
    private String status;

}
