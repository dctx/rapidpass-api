package ph.devcon.rapidpass.controllers;

import org.assertj.core.data.Offset;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassCSVDownloadData;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistryBatchRestController.class)
public class RegistryBatchRestControllerTest
{
    private static Logger LOGGER = Logger.getLogger(RegistryBatchRestControllerTest.class.getName());
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    RegistryService mockRegistryService;
    
    @Test
    public void downloadAccessApprovedPassCsv() throws Exception
    {
        List<RapidPassCSVDownloadData> sampleList = new ArrayList<>();
        for(int i = 0 ; i < 10;i++)
        {
            sampleList.add(prepareSampleCsvData());
        }
        OffsetDateTime now = OffsetDateTime.now();
        
        Pageable pageable = PageRequest.of(0,2);
        Page<RapidPassCSVDownloadData> page = new PageImpl<RapidPassCSVDownloadData>(sampleList,pageable,sampleList.size());
        
        when(mockRegistryService.findAllApprovedOrSuspendedRapidPassCsvAfter(now,pageable)).thenReturn(page);
        final MockHttpServletResponse response = mockMvc.perform(get("/batch/access-passes?lastSyncOn={syncOn}&pageNumber{}&pageSize={}",now.toEpochSecond(),0,2))
            .andExpect(status().isOk())
            .andReturn().getResponse();
        LOGGER.log(Level.INFO, response.getContentAsString());
        assertThat(response.getContentType(),is(MediaType.APPLICATION_JSON_VALUE));

        
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
