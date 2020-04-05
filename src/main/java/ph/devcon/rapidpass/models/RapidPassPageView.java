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
    private int totalPages;
    private int totalElements;
    private List<RapidPass> rapidPassList;

}
