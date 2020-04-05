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
        columnList.add("controlCode");
        columnList.add("passType");
        columnList.add("aporType");
        columnList.add("validTo");
        columnList.add("name");
        columnList.add("plateNumber");
        columnList.add("status");
        return columnList;
    }

    public static List<?> values(AccessPass accessPass) {
        List<Object> valueList = new ArrayList<>();
        valueList.add(accessPass.getControlCode());
        valueList.add(accessPass.getPassType());
        valueList.add(accessPass.getAporType());
        valueList.add(accessPass.getValidTo().toEpochSecond());
        valueList.add(accessPass.getName());
        valueList.add(accessPass.getPlateNumber());
        valueList.add(accessPass.getStatus());
        return valueList;
    }
}
