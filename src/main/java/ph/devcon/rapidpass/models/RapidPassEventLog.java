package ph.devcon.rapidpass.models;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.AccessPassEvent;
import ph.devcon.rapidpass.utilities.ControlCodeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Paged CSV with meta data for client to know what page has been downloaded
 */
@ApiModel(description = "Paged CSV with meta data for client to know what page has been downloaded")
@Validated
@Data
@Builder
public class RapidPassEventLog {

    private PageMetaData meta;
    private List<?> data;


    public static List<String> getColumnNames() {
        List<String> columnList = new ArrayList<>();
        columnList.add("eventID");
        columnList.add("referenceID");
        columnList.add("passType");
        columnList.add("aporType");
        columnList.add("controlCode");
        columnList.add("name");
        columnList.add("plateNumber");
        columnList.add("status");
        columnList.add("validFrom");
        columnList.add("validTo");
        columnList.add("eventTimestamp");
        return columnList;
    }

    public static List<?> values(AccessPassEvent a, String secretKey) {
        List<Object> valueList = new ArrayList<>();
        valueList.add(a.getId());
        valueList.add(a.getReferenceId());
        valueList.add(a.getPassType());
        valueList.add(a.getAporType());
        valueList.add(ControlCodeGenerator.generate(secretKey, a.getId()));
        valueList.add(a.getName());
        valueList.add(a.getPlateNumber());
        valueList.add(a.getStatus());
        valueList.add(a.getValidFrom().toEpochSecond());
        valueList.add(a.getValidTo().toEpochSecond());
        valueList.add(a.getValidTo().toEpochSecond());
        valueList.add(a.getEventTimestamp().toEpochSecond());
        return valueList;
    }

    public static RapidPassEventLog buildFrom(Page<AccessPassEvent> accessPassEventPage, String secretKey) {
        List<?> rapidPassEvents = accessPassEventPage.getContent().stream()
                .map(a -> RapidPassEventLog.values(a, secretKey))
                .collect(Collectors.toList());
        return RapidPassEventLog.builder()
                .meta(PageMetaData.builder()
                        .currentPage(accessPassEventPage.getPageable().getPageNumber())
                        .currentPageRows(accessPassEventPage.getNumberOfElements())
                        .totalPages(accessPassEventPage.getTotalPages())
                        .totalRows(accessPassEventPage.getTotalElements())
                        .columnNames(getColumnNames())
                        .build())
                .data(rapidPassEvents)
                .build();
    }
}
