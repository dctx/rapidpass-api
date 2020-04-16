package ph.devcon.rapidpass.controllers;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.models.RapidPassEventLog;
import ph.devcon.rapidpass.services.RegistryService;
import ph.devcon.rapidpass.utilities.csv.ApproverRegistrationCsvProcessor;
import ph.devcon.rapidpass.utilities.csv.SubjectRegistrationCsvProcessor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * Registry API Rest Controller specifically for batch operations
 */
@CrossOrigin
@RestController
@RequestMapping("/batch")
@Validated
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

        return this.registryService.batchUploadRapidPassRequest(approvedAccessPass);
    }

    @GetMapping(value = "/access-passes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> downloadAccessPasses(
            @NotNull @ApiParam(value = "indicates last sync of checkpoint device in Epoch format", required = true)
            @Valid @RequestParam(value = "lastSyncOn", required = true)
                    Long lastSyncOn,
            @NotNull @ApiParam(value = "page number requested", required = true)
            @Valid @RequestParam(value = "pageNumber", required = false, defaultValue = "0")
                    Integer pageNumber, @ApiParam(value = "size of page requested")
            @Valid @RequestParam(value = "pageSize", required = false, defaultValue = "1000")
                    Integer pageSize) {

//        OffsetDateTime lastSyncDateTime =
//                OffsetDateTime.of(LocalDateTime.ofEpochSecond(lastSyncOn, 0, ZoneOffset.UTC), ZoneOffset.UTC);
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);

//        return ResponseEntity.ok().body(registryService.findAllApprovedSince(lastSyncDateTime, pageable));

        // closing off /batch/access-passes due to PII exposure and checkpoint not yet authenticating
        return ResponseEntity.ok().body(new String[]{});
    }

    @GetMapping("/access-pass-events")
    public ResponseEntity<?> getAccessPassEvents(
            @RequestParam @Min(0) Integer fromEventID,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "1000") Integer pageSize)
    {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        RapidPassEventLog rapidPassEventLog = registryService.getAccessPassEvent(fromEventID, page);
        if (rapidPassEventLog != null) {
            return ResponseEntity.ok(rapidPassEventLog);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/approvers")
    public List<String> batchRegisterApprovers(@RequestParam("file") MultipartFile csvFile) throws IOException {
        ApproverRegistrationCsvProcessor processor = new ApproverRegistrationCsvProcessor();

        List<AgencyUser> agencyUsers = processor.process(csvFile);

        return this.registryService.batchUploadApprovers(agencyUsers);
    }
}

