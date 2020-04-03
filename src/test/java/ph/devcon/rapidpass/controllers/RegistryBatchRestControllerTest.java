package ph.devcon.rapidpass.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.config.JwtSecretsConfig;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassCSVDownloadData;
import ph.devcon.rapidpass.services.RegistryService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistryBatchRestController.class)
@EnableConfigurationProperties
@Import(JwtSecretsConfig.class)
public class RegistryBatchRestControllerTest
{

    private static final String API_KEY_HEADER = "RP-API-KEY";
    private static final String API_KEY_VALUE = "dctx";

    private static Logger LOGGER = Logger.getLogger(RegistryBatchRestControllerTest.class.getName());
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    RegistryService mockRegistryService;
    
    @Test
    public void downloadAccessApprovedPassCsv() throws Exception
    {
        final int pageSize = 2;
        final int totalRows = 10;
        List<RapidPassCSVDownloadData> sampleList = new ArrayList<>();
        
        for(int i = 0 ; i < pageSize;i++)
        {
            sampleList.add(prepareSampleCsvData());
        }
        OffsetDateTime now = OffsetDateTime.now();
        
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<RapidPassCSVDownloadData> page = new PageImpl<RapidPassCSVDownloadData>(sampleList,pageable,totalRows);
        
        when(mockRegistryService.findAllApprovedOrSuspendedRapidPassCsvAfter(any(), any())).thenReturn(page);
        
        mockMvc.perform(get("/batch/access-passes?lastSyncOn={lastSyncOn}&pageNumber{pageNumber}&pageSize={pageSize}",now.toEpochSecond(),0, pageSize)
            .header(API_KEY_HEADER, API_KEY_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meta.pageNumber").value("0"))
            .andExpect(jsonPath("$.meta.pageSize").value("2"))
            .andExpect(jsonPath("$.meta.totalPages").value("5"))
            .andExpect(jsonPath("$.meta.totalRows").value("10"))
            .andExpect(jsonPath("$.csv").isString())
            .andDo(print());
    }
    
    
    private RapidPassCSVDownloadData prepareSampleCsvData()
    {
        OffsetDateTime now = OffsetDateTime.now();
        return RapidPassCSVDownloadData.builder()
                .controlCode("ControlCode")
                .passType(PassType.INDIVIDUAL.toString())
                .aporType("MM")
                .validFrom(now.toEpochSecond())
                .validUntil(now.toEpochSecond())
                .idType("PERSONAL")
                .identifierNumber("NP-030303-1")
                .status(AccessPassStatus.APPROVED.toString())
                .issuedOn(now.toEpochSecond())
                .build();
    }
}
