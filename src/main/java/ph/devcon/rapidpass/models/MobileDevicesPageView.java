package ph.devcon.rapidpass.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.devcon.rapidpass.api.models.MobileDevice;

import java.util.List;

/**
 * Paged View of scanner mobile devices.
 *
 * @author jonasespelita@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MobileDevicesPageView extends AbstractPageView {

    @SuppressWarnings("unused")
    @Builder
    public MobileDevicesPageView(int currentPage,
                                 int currentPageRows,
                                 int totalPages,
                                 long totalRows,
                                 boolean isFirstPage,
                                 boolean isLastPage,
                                 boolean hasNext,
                                 boolean hasPrevious,
                                 List<MobileDevice> data) {
        super(currentPage, currentPageRows, totalPages, totalRows, isFirstPage, isLastPage, hasNext, hasPrevious);
        this.data = data;
    }

    private final List<MobileDevice> data;
}
