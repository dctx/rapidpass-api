package ph.devcon.rapidpass.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * This the JSON format when performing a batch upload of the user data expected by the backend.
 *
 * TODO: This is incorrect, as the batch data is uploaded not using JSON data (as arrays) but instead uploaded as a CSV.
 *
 * The {@link RapidPassBatchRequest} class models a Rapid Pass Request.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RapidPassBatchRequest {
    /**
     * Backend only reference number.
     */
    @NotNull
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private String refNum = UUID.randomUUID().toString();

    @NotNull
    private List<RapidPass> batch;

}
