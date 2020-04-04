package ph.devcon.rapidpass.controllers;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.models.RapidPassBulkData;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.utilities.csv.SubjectRegistrationCsvProcessor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


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
    public ResponseEntity<RapidPassBulkData> downloadAccessPasses(
            @NotNull @ApiParam(value = "indicates last sync of checkpoint device in Epoch format", required = true)
            @Valid @RequestParam(value = "lastSyncOn", required = true)
                    Long lastSyncOn,
            @NotNull @ApiParam(value = "page number requested", required = true)
            @Valid @RequestParam(value = "pageNumber", required = false, defaultValue = "0")
                    Integer pageNumber, @ApiParam(value = "size of page requested")
            @Valid @RequestParam(value = "pageSize", required = false, defaultValue = "1000")
                    Integer pageSize) {

        OffsetDateTime lastSyncDateTime = null;
        if (lastSyncOn == 0) {
            lastSyncDateTime = OffsetDateTime.now().minusDays(1);
        } else {
            lastSyncDateTime =
                    OffsetDateTime.of(LocalDateTime.ofEpochSecond(lastSyncOn, 0, ZoneOffset.UTC), ZoneOffset.UTC);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return ResponseEntity.ok().body(registryService.findAllApprovedSince(lastSyncDateTime, pageable));
    }

    @PostMapping("approvers")
    public void batchRegisterApprovers(@RequestParam("file") MultipartFile csvFile) {

    }

}

