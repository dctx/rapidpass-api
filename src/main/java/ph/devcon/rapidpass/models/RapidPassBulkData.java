package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.devcon.rapidpass.entities.AccessPass;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapidPassBulkData {
    private int currentPage;
    private int currentPageRows;
    private int totalPages;
    private long totalRows;
    private List<?> data;


    public static List<?> getColumnNames() {
        List<Object> columnList = new ArrayList<>();
        columnList.add("passType");
        columnList.add("aporType");
        columnList.add("controlCode");
        columnList.add("name");
        columnList.add("status");
        columnList.add("idType");
        columnList.add("identifierNumber");
        columnList.add("plateNumber");
        columnList.add("validFrom");
        columnList.add("validTo");
        columnList.add("issuedBy");
        return columnList;
    }

    public static List<?> values(AccessPass accessPass) {
        List<Object> valueList = new ArrayList<>();
        valueList.add(accessPass.getPassType());
        valueList.add(accessPass.getAporType());
        valueList.add(accessPass.getControlCode());
        valueList.add(accessPass.getName());
        valueList.add(accessPass.getStatus());
        valueList.add(accessPass.getIdType());
        valueList.add(accessPass.getIdentifierNumber());
        valueList.add(accessPass.getPlateNumber());
        valueList.add(accessPass.getValidFrom().toEpochSecond());
        valueList.add(accessPass.getValidTo().toEpochSecond());
        valueList.add(accessPass.getIssuedBy());
        return valueList;
    }
}
