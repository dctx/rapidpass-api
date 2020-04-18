/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

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