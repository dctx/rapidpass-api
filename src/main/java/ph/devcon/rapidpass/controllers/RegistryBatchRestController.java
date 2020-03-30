package ph.devcon.rapidpass.controllers;


import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassBatchRequest;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.services.RegistryService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
     * @param username Registrar User name
     *
     */
    @PostMapping("/access-passes")
    Iterable<RapidPass> newRequestPass(@RequestParam("file") MultipartFile csvFile)
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

                fileReader.close();
            } catch (Exception e) {
                throw e;
            }
        }
        return this.registryService.batchUpload(approvedAccessPass);
    }
}

