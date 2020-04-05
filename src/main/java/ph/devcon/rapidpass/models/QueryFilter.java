package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {

    public static final Integer DEFAULT_PAGE_SIZE = 25;

    private String passType;
    private String referenceId;
    private String status;
    private String aporType;
    private String plateNumber;
    private Integer pageNo = 0;
    private Integer pageSize = DEFAULT_PAGE_SIZE;
}
