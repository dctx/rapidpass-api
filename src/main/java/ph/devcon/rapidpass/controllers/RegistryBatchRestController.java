package ph.devcon.rapidpass.controllers;


import com.opencsv.bean.*;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.models.*;
import ph.devcon.rapidpass.services.RegistryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
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
     * @param csvFile  Receives CSV File Payload
     */
    @PostMapping("/access-passes")
    Iterable<String> newRequestPass(@RequestParam("file") MultipartFile csvFile)
            throws IOException, RegistryService.UpdateAccessPassException {

        List<RapidPassCSVdata> approvedAccessPass;

        if (csvFile.isEmpty()) {
            return null;
        } else {

            try (Reader fileReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
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
                        "plateNumber",
                        "mobileNumber",
                        "email",
                        "originName",
                        "originStreet",
                        "originCity",
                        "originProvince",
                        "destName",
                        "destStreet",
                        "destCity",
                        "destProvince",
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

                fileReader.close();
            } catch (Exception e) {
                throw e;
            }
        }
        return this.registryService.batchUpload(approvedAccessPass);
    }

    @GetMapping(value = "/access-passes", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RapidPassBulkData> downloadAccesPasses(
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
}

