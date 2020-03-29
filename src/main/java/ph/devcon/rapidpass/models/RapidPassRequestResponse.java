package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * The response for a Rapid Pass Request. This returns the referenceId.
 *
 * TODO: Find a better name
 */
@Data
@Builder
@JsonInclude(NON_NULL)
public class RapidPassRequestResponse {

    @NotNull
    private String referenceId;

}
