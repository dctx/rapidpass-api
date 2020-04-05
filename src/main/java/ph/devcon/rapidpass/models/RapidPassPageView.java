package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents to view that the user sees when browsing through a list of rapid pass
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapidPassPageView {

    private int currentPage;
    private int currentPageRows;
    private int totalPages;
    private long totalRows;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<RapidPass> rapidPassList;

}
