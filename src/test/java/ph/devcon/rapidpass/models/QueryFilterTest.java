package ph.devcon.rapidpass.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryFilterTest {

    QueryFilter queryFilter;

    @BeforeEach
    void setUp() {
        queryFilter = new QueryFilter();
    }

    @Test
    void testDefaultPageNo() {
        assertNotNull(queryFilter.getPageNo());
    }

    @Test
    void testDefaultPageSize() {
        assertNotNull(queryFilter.getMaxPageRows());
        assertEquals(QueryFilter.DEFAULT_PAGE_SIZE, queryFilter.getMaxPageRows());
    }
}