package ph.devcon.rapidpass.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Abstract class that defines a page view.
 *
 * @author jonasespelita@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPageView {
    private int currentPage;
    private int currentPageRows;
    private int totalPages;
    private long totalRows;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasNext;
    private boolean hasPrevious;
}
