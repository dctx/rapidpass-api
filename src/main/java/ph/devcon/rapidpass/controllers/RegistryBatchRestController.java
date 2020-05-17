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

import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.exceptions.CsvColumnMappingMismatchException;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Registry API Rest Controller specifically for batch operations
 */
@RestController
@RequestMapping("/batch")
@Validated
@Slf4j
public class RegistryBatchRestController {

    private RegistryService registryService;

    @Value("${endpointswitch.batch.accesspasses:false}")
    private boolean enableBatchDownloadAccessPasses;

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
    Iterable<String> newRequestPass(@RequestParam("file") MultipartFile csvFile, Principal principal)
            throws IOException, RegistryService.UpdateAccessPassException, CsvColumnMappingMismatchException, CsvRequiredFieldEmptyException {

        SubjectRegistrationCsvProcessor processor = new SubjectRegistrationCsvProcessor();
        List<RapidPassCSVdata> approvedAccessPass = processor.process(csvFile);

        return this.registryService.batchUploadRapidPassRequest(approvedAccessPass, principal);
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

        if (!enableBatchDownloadAccessPasses) {
            return ResponseEntity.ok().body(new String[]{});
        }

//         closing off /batch/access-passes due to PII exposure and checkpoint not yet authenticating
        OffsetDateTime lastSyncDateTime =
                OffsetDateTime.of(LocalDateTime.ofEpochSecond(lastSyncOn, 0, ZoneOffset.UTC), ZoneOffset.UTC);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return ResponseEntity.ok().body(registryService.findAllApprovedSince(lastSyncDateTime, pageable));
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
    public List<String> batchRegisterApprovers(@RequestParam("file") MultipartFile csvFile) throws IOException, CsvColumnMappingMismatchException, CsvRequiredFieldEmptyException {
        ApproverRegistrationCsvProcessor processor = new ApproverRegistrationCsvProcessor();

        List<AgencyUser> agencyUsers = processor.process(csvFile);

        return this.registryService.batchUploadApprovers(agencyUsers);
    }
}

