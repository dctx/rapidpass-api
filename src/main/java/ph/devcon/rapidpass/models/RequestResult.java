package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestResult {
    private String referenceId;
    /**
     * Can either be "APPROVED" or "DECLINED"
     */
    private String result;
    /**
     * A little ambiguous.
     *
     * Receiving data from the clients, the `remarks` property of an `AccessPass` or `RapidPass` is used to denote
     * why they need the rapid pass. This data matches to the table data column `remarks`.
     *
     * However, the usage of `remarks` here (when requesting for the status of a rapid pass) is for specifying the
     * reason for why the access pass was declined. This data matches to the table data column `updates`.
     */
    private String remarks;
}
