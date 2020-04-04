package ph.devcon.rapidpass.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.models.LookupValue;
import ph.devcon.rapidpass.services.LookupTableService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@Slf4j
@RequestMapping("/lookup")
public final class LookupController {

    private final LookupTableService lookupTableService;

    @Autowired
    public LookupController(final LookupTableService lookupTableService) {
        this.lookupTableService = lookupTableService;
    }

    /**
     * Retrieve data from the lookup table
     *
     * @param lookupType the type of lookup data to retrieve
     * @return a Map of lookup type and the list of data
     */
    @GetMapping
    public final ResponseEntity<Map<String, List<LookupValue>>> lookup(@RequestParam("type") final String lookupType) {
        // return 404 if invalid
        LookupType type;
        try {
            type = LookupType.valueOf(lookupType);
        } catch(final IllegalArgumentException e) {
            log.error("lookup type provided is not valid: {}", lookupType);
            return ResponseEntity.badRequest().build();
        }
        // return lookup map if valid (future possibility of just return all lookups?)
        try {
            final List<LookupTable> lookups = this.lookupTableService.getByType(type);
            final Map<String, List<LookupValue>> data = new HashMap<>();
            if (CollectionUtils.isEmpty(lookups)) {
                log.warn("an empty lookup was returned");
                return ResponseEntity.ok(data);
            }
            for (final LookupTable lt : lookups) {
                final String key = lt.getLookupTablePK().getKey();
                List<LookupValue> lv = data.get(key);
                if (lv == null) {
                    lv = new ArrayList<>();
                    data.put(key, lv);
                }
                lv.add(LookupValue.from(lt));
            }
            return ResponseEntity.ok(data);
        } catch (final Exception e) {
            // return 500 for unknown error... TODO: mostly database error at this point.
            log.error("error retrieving lookup from the database: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
