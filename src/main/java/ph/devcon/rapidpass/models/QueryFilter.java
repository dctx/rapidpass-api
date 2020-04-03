package ph.devcon.rapidpass.models;

import lombok.Data;

@Data
public class QueryFilter {

    public static final Integer DEFAULT_PAGE_SIZE = 25;

    private String status;
    private String validUntil;
    private Integer pageNo;
    private Integer pageSize;
    private String aporType;
}
