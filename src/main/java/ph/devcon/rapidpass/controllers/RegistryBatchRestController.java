package ph.devcon.rapidpass.controllers;


import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.models.PageMetaData;
import ph.devcon.rapidpass.models.PagedCSV;
import ph.devcon.rapidpass.models.RapidPassCSVDownloadData;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.utilities.csv.SubjectRegistrationCsvProcessor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;


/**
 * Registry API Rest Controller specifically for batch operations
 */
@CrossOrigin
@RestController
@RequestMapping("/batch")
@Slf4j
public class RegistryBatchRestController {

    private RegistryService registryService;

    @Autowired
    public RegistryBatchRestController(RegistryService registryService) {
        this.registryService = registryService;
    }

    /**
     * Upload CSV or excel file of approved control numbers
     *
     * @param csvFile Receives CSV File Payload
     *
     */
    @PostMapping("/access-passes")
    Iterable<String> newRequestPass(@RequestParam("file") MultipartFile csvFile)
            throws IOException, RegistryService.UpdateAccessPassException {

        SubjectRegistrationCsvProcessor processor = new SubjectRegistrationCsvProcessor();
        List<RapidPassCSVdata> approvedAccessPass = processor.process(csvFile);

        return this.registryService.batchUpload(approvedAccessPass);
    }
    
    @GetMapping(value = "/access-passes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity downloadAccesPasses(
        @NotNull @ApiParam(value = "indicates last sync of checkpoint device in Epoch format", required = true)
        @Valid @RequestParam(value = "lastSyncOn", required = true)
            Long lastSyncOn,
        @NotNull @ApiParam(value = "page number requested", required = true)
        @Valid @RequestParam(value = "pageNumber", required = false,defaultValue = "0")
            Integer pageNumber,@ApiParam(value = "size of page requested")
        @Valid @RequestParam(value = "pageSize", required = false,defaultValue = "1000")
            Integer pageSize)
    {
        ResponseEntity response;
        try
        {
            OffsetDateTime lastSyncDateTime =
                OffsetDateTime.of(LocalDateTime.ofEpochSecond(lastSyncOn, 0, ZoneOffset.UTC), ZoneOffset.UTC);
            Pageable pageable = PageRequest.of(pageNumber,pageSize);
            final Page<RapidPassCSVDownloadData> pagedRapidPass = registryService.findAllApprovedOrSuspendedRapidPassCsvAfter(lastSyncDateTime,pageable);
            StringWriter writer = new StringWriter();
            ICSVWriter csvWriter = new CSVWriter(writer);
            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(csvWriter)
                .withSeparator(DEFAULT_SEPARATOR)
                .build();
            
            sbc.write(pagedRapidPass.getContent());
            final String rapidPassCsv = writer.getBuffer().toString();
            
            PageMetaData pageMetaData = new PageMetaData();
            pageMetaData.setPageNumber(pagedRapidPass.getNumber());
            pageMetaData.setPageSize(pagedRapidPass.getSize());
            pageMetaData.setTotalPages(pagedRapidPass.getTotalPages());
            pageMetaData.setTotalRows(pagedRapidPass.getTotalElements());
            PagedCSV pagedCSV = new PagedCSV();
            pagedCSV.setCsv(rapidPassCsv);
            pagedCSV.setMeta(pageMetaData);
            
            response = new ResponseEntity(pagedCSV, HttpStatus.OK);
        }
        catch (Exception e)
        {
            response = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
        
    }

    @PostMapping("approvers")
    public void batchRegisterApprovers(@RequestParam("file") MultipartFile csvFile) {

    }

}

