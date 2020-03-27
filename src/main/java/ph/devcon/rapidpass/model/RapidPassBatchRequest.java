package ph.devcon.rapidpass.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

import static ph.devcon.rapidpass.model.RapidPassRequest.RequestStatus.PENDING;

/**
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
