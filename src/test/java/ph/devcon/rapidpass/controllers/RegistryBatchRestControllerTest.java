package ph.devcon.rapidpass.controllers;

import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
    public void downloadAccessPassCsv() throws Exception
    {
        RapidPass sampleRapidPass = prepareSampleData();
    
        List<RapidPass> sampleList = new ArrayList<>();
        sampleList.add(sampleRapidPass);
        when(mockRegistryService.findAllRapidPasses(Optional.of(Pageable.unpaged()))).thenReturn(sampleList);
        final MockHttpServletResponse response = mockMvc.perform(get("/batch/access-passes"))
            .andExpect(status().isOk())
            .andReturn().getResponse();
        LOGGER.log(Level.INFO, response.getContentAsString());
        assertThat(response.getContentType(),is(MediaType.TEXT_PLAIN_VALUE));
        assertThat(response.getContentAsString(),containsString("\"APORTYPE\",\"COMPANY\",\"CONTROLCODE\",\"DESTCITY\",\"DESTNAME\",\"DESTPROVINCE\",\"DESTSTREET\",\"IDENTIFIERNUMBER\",\"IDTYPE\",\"NAME\",\"PASSTYPE\",\"REFERENCEID\",\"REMARKS\",\"STATUS\",\"VALIDFROM\",\"VALIDUNTIL\""));
        assertThat(response.getContentAsString(),containsString("\"MM\",\"MyCompany\",\"ControlCode\",\"Pasig\",\"MyFriend\",\"MM\",\"ADB Avenue\",\"MYIDNo\",\"PERSONAL\",\"Sample Name\",\"INDIVIDUAL\",\"myreferenceid\",\"I need this\",\"APPROVED\",\"\",\"\""));
        
    }
    
    @Test
    public void downloadAccessPassCompressed() throws Exception
    {
        RapidPass sampleRapidPass = prepareSampleData();
    
        List<RapidPass> sampleList = new ArrayList<>();
        sampleList.add(sampleRapidPass);
        when(mockRegistryService.findAllRapidPasses(Optional.of(Pageable.unpaged()))).thenReturn(sampleList);
        final MockHttpServletResponse response = mockMvc.perform(get("/batch/access-passes?compressed=true"))
            .andExpect(status().isOk())
            .andReturn().getResponse();
        
        try(ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(response.getContentAsByteArray()));)
        {
            ZipEntry zipEntry = zis.getNextEntry();
            byte[] readBuffer = new byte[1024];
            while (zipEntry != null)
            {
                assertThat("Zip contains csv",zipEntry.getName(),containsString(".csv"));
                File csvFile = new File(zipEntry.getName());
                try (FileOutputStream csvFileOs = new FileOutputStream(csvFile);)
                {
                    int readLength;
                    while ((readLength = zis.read(readBuffer)) > 0)
                    {
                        csvFileOs.write(readBuffer, 0, readLength);
                    }
                    assertThat("csv file is extracted!", csvFile, is(FileMatchers.anExistingFile()));
                }
                finally
                {
                    // cleanup
                    csvFile.delete();
                }
                zipEntry = zis.getNextEntry();
            }
        }
        
    }
    
    private RapidPass prepareSampleData()
    {
        return RapidPass.builder()
                .name("Sample Name")
                .controlCode("ControlCode")
                .passType(PassType.INDIVIDUAL)
                .aporType("MM")
                .company("MyCompany")
                .identifierNumber("MYIDNo")
                .idType("PERSONAL")
                .status("APPROVED")
                .referenceId("myreferenceid")
                .destCity("Pasig")
                .destName("MyFriend")
                .destProvince("MM")
                .destStreet("ADB Avenue")
                .remarks("I need this").build();
    }
}
