package ph.devcon.rapidpass.models;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ph.devcon.rapidpass.entities.AccessPassEvent;

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
public class PagedAccessPassEvent {

    private PageMetaData meta;
    private List<RapidPassEvent> data;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PagedAccessPassEvent pagedAccessPassEvent = (PagedAccessPassEvent) o;
        return Objects.equals(this.meta, pagedAccessPassEvent.meta) &&
                Objects.equals(this.data, pagedAccessPassEvent.data);
    }

    public static PagedAccessPassEvent buildFrom(Page<AccessPassEvent> accessPassEventPage, String secretKey) {
        List<RapidPassEvent> rapidPassEvents = accessPassEventPage.getContent().stream()
                .map(a -> RapidPassEvent.buildFrom(a, secretKey))
                .collect(Collectors.toList());
        return PagedAccessPassEvent.builder()
                .meta(PageMetaData.builder()
                        .currentPage(accessPassEventPage.getPageable().getPageNumber())
                        .currentPageRows(accessPassEventPage.getNumberOfElements())
                        .totalPages(accessPassEventPage.getTotalPages())
                        .totalRows(accessPassEventPage.getTotalElements())
                        .build())
                .data(rapidPassEvents)
                .build();
    }
}
