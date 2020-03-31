package ph.devcon.rapidpass.controllers;


import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.*;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.services.RegistryService;

import javax.validation.Valid;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;


/**
 * Registry API Rest Controller specifically for batch operations
 */
@CrossOrigin
@RestController
@RequestMapping("/batch")
@Slf4j
public class RegistryBatchRestController
{
    
    private RegistryService registryService;
    
    @Autowired
    public RegistryBatchRestController(RegistryService registryService)
    {
        this.registryService = registryService;
    }
    
    /**
     * Upload CSV or excel file of approved control numbers
     *
     * @param csvFile  Receives CSV File Payload
     * @param username Registrar User name
     */
    @PostMapping("/access-passes")
    public Iterable<RapidPass> newRequestPass(@RequestParam("file") MultipartFile csvFile)
        throws IOException, RegistryService.UpdateAccessPassException
    {
    
        List<RapidPassCSVdata> approvedAccessPass;
    
        if (csvFile.isEmpty())
        {
            return null;
        }
        else
        {
        
            try (Reader fileReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream())))
            {
                ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
                strategy.setType(RapidPassCSVdata.class);
                String[] accessPassCSVColumnMapping = {
                    "passType",
                    "aporType",
                    "firstName",
                    "middleName",
                    "lastName",
                    "suffix",
                    "company",
                    "idType",
                    "identifierNumber",
                    "mobileNumber",
                    "email",
                    "originName",
                    "originStreet",
                    "originCity",
                    "destName",
                    "destStreet",
                    "destCity",
                    "remarks"
                };
            
                strategy.setColumnMapping(accessPassCSVColumnMapping);
                CsvToBean<RapidPassCSVdata> csvToBean = new CsvToBeanBuilder(fileReader)
                    .withMappingStrategy(strategy)
                    .withType(RapidPassCSVdata.class)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            
                approvedAccessPass = csvToBean.parse();
                
            }
        }
        return this.registryService.batchUpload(approvedAccessPass);
    }
    
    @GetMapping(value = "/access-passes", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity downloadAccesPasses(
        @ApiParam(value = "Set the status to be downloaded",
            allowableValues = "PENDING, APPROVED, DECLINED")
        @Valid @RequestParam(value = "status", required = false, defaultValue = "APPROVED") String status,
        @ApiParam(value = "specifies whether to compress the csv file or not, default is false")
        @Valid @RequestParam(value = "compressed", required = false, defaultValue = "false") boolean compressed)
    {
        ResponseEntity response;
        try
        {
            final List<RapidPass> allRapidPasses = registryService.findAllRapidPasses(Optional.of(Pageable.unpaged()));
            StringWriter writer = new StringWriter();
            ICSVWriter csvWriter = new CSVWriter(writer);
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(csvWriter)
                .withSeparator(DEFAULT_SEPARATOR)
                .build();
            sbc.write(allRapidPasses);
            final String rapidPassCsv = writer.getBuffer().toString();
            HttpHeaders headers = new HttpHeaders();
            if (compressed)
            {
            
                byte[] compressedCsv = convertStringToZippedBytes(rapidPassCsv, "RapidPass-");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                response = new ResponseEntity(compressedCsv, headers, HttpStatus.OK);
            }
            else
            {
                headers.setContentType(MediaType.TEXT_PLAIN);
                response = new ResponseEntity(rapidPassCsv, headers, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    
    }
    
    private byte[] convertStringToZippedBytes(String rapidPassCsv, String filePrefix) throws IOException
    {
        byte[] compressedCsv;
        try(
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)
        )
        {
            OffsetDateTime dateTime = OffsetDateTime.now();
            String fileName = new StringBuilder(filePrefix)
                .append(dateTime.getYear()).append("-")
                .append(dateTime.getMonth()).append("-")
                .append(dateTime.getDayOfMonth()).append("-")
                .append(dateTime.getHour()).append("-")
                .append(dateTime.getMinute()).append("-")
                .append(dateTime.getSecond()).append(".csv").toString();
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(rapidPassCsv.getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.finish();
            zipOutputStream.flush();
            compressedCsv = byteArrayOutputStream.toByteArray();
        }
        return compressedCsv;
    }
}



