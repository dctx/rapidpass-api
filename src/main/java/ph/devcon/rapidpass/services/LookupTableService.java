package ph.devcon.rapidpass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.repositories.LookupTableRepository;

import java.util.List;

@Service
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
        return lookupTableRepository.getAllByLookupTablePKKey("IDTYPE-I");
    }

    public List<LookupTable> getVehicleIdTypes() {
        return lookupTableRepository.getAllByLookupTablePKKey("IDTYPE-V");
    }
}
