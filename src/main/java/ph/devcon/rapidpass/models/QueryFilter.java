package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.devcon.rapidpass.enums.RecordSource;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilter {

    public static final Integer DEFAULT_PAGE_SIZE = 25;

    private String passType;
    private String aporType;
    private String referenceId;
    private String status;
    private String plateNumber;
    private RecordSource source; // ONLINE, BULK_UPLOAD
    private Integer pageNo = 0;
    private Integer maxPageRows = DEFAULT_PAGE_SIZE;
}
