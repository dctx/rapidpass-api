package ph.devcon.rapidpass.services;

import org.springframework.beans.factory.annotation.Autowired;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.repositories.LookupTableRepository;

import java.util.List;

public class LookupTableService {

    private LookupTableRepository lookupTableRepository;

    @Autowired
    LookupTableService(LookupTableRepository lookupTableRepository) {
        this.lookupTableRepository = lookupTableRepository;
    }

    public List<LookupTable> getAporTypes() {
        return lookupTableRepository.getAllByLookupTablePKKey("APOR");
    }

    public List<LookupTable> getIndividualIdTypes() {
        return lookupTableRepository.getAllByLookupTablePKKey("IDTYPE-IND");
    }

    public List<LookupTable> getVehicleIdTypes() {
        return lookupTableRepository.getAllByLookupTablePKKey("IDTYPE-VHC");
    }
}
