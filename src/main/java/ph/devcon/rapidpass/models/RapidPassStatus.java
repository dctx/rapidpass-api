package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;

import javax.validation.constraints.NotNull;


/**
 * This JSON Body definition is used in two places:
 *
 * 1. When an Approver wishes to update the status of an {@link AccessPass} to either "APPROVED" or "DECLINED", this
 * JSON body is sent. When the status is set to declined, the Approver may optionally specify a reason why they
 * were declined.
 *
 * 2. When a public user wishes to find out the status of their {@link AccessPass}, this JSON Body is the response of
 * that query.
 */
@Builder
@Data
public class RapidPassStatus {
    private String referenceId;

    /**
     * Can either be "APPROVED" or "DECLINED"
     */
    @NotNull(message = "status cannot be blank")
    private AccessPassStatus status;

    /**
     * This is the reason why their request was declined. This is null if the status is "APPROVED".
     *
     * This data matches to the table data column `updates`.
     */
    private String remarks;
}
