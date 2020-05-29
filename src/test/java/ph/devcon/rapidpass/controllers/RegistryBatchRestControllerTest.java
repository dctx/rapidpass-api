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

package ph.devcon.rapidpass.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegistryBatchRestControllerTest
{
    public void downloadAccessApprovedPassCsv() throws Exception
    {
//        final int pageSize = 2;
//        final int totalRows = 10;
//        List<RapidPassCSVDownloadData> sampleList = new ArrayList<>();
//
//        for(int i = 0 ; i < pageSize;i++)
//        {
//            sampleList.add(prepareSampleCsvData());
//        }
//        OffsetDateTime now = OffsetDateTime.now();
//
//        Pageable pageable = PageRequest.of(0, pageSize);
//        Page<RapidPassCSVDownloadData> page = new PageImpl<RapidPassCSVDownloadData>(sampleList,pageable,totalRows);
        
//        when(mockRegistryService.findAllApprovedOrSuspended(any(), any())).thenReturn(page);
//
//        mockMvc.perform(get("/batch/access-passes?lastSyncOn={lastSyncOn}&pageNumber{pageNumber}&pageSize={pageSize}",now.toEpochSecond(),0, pageSize)
//            .header(API_KEY_HEADER, API_KEY_VALUE))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.meta.pageNumber").value("0"))
//            .andExpect(jsonPath("$.meta.pageSize").value("2"))
//            .andExpect(jsonPath("$.meta.totalPages").value("5"))
//            .andExpect(jsonPath("$.meta.totalRows").value("10"))
//            .andExpect(jsonPath("$.csv").isString())
//            .andDo(print());
    }
    
    
//    private RapidPassCSVDownloadData prepareSampleCsvData()
//    {
//        OffsetDateTime now = OffsetDateTime.now();
//        return RapidPassCSVDownloadData.builder()
//                .controlCode("ControlCode")
//                .passType(PassType.INDIVIDUAL.toString())
//                .aporType("MM")
//                .validFrom(now.toEpochSecond())
//                .validUntil(now.toEpochSecond())
//                .idType("PERSONAL")
//                .identifierNumber("NP-030303-1")
//                .status(AccessPassStatus.APPROVED.toString())
//                .issuedOn(now.toEpochSecond())
//                .build();
//    }
}
