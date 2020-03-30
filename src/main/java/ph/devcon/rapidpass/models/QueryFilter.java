package ph.devcon.rapidpass.models;

import lombok.Data;

@Data
public class QueryFilter {
    private String status;
    private String validUntil;
    private Integer pageNo;
    private Integer batchSize;
}
