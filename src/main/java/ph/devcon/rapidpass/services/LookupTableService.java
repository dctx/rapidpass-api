package ph.devcon.rapidpass.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.repositories.LookupTableRepository;

import java.util.List;

import static ph.devcon.rapidpass.enums.LookupType.APOR;
import static ph.devcon.rapidpass.enums.LookupType.ID_TYPE_INDIVIDUAL;
import static ph.devcon.rapidpass.enums.LookupType.ID_TYPE_VEHICLE;

@Service
public class LookupTableService {

    private LookupTableRepository lookupTableRepository;

    @Autowired
    LookupTableService(LookupTableRepository lookupTableRepository) {
        this.lookupTableRepository = lookupTableRepository;
    }

    public List<LookupTable> getByType(LookupType type) {
        return this.lookupTableRepository.getAllByLookupTablePKKey(type.toDBType());
    }

    public List<LookupTable> getAporTypes() {
        return this.getByType(APOR);
    }

    public List<LookupTable> getIndividualIdTypes() {
        return this.getByType(ID_TYPE_INDIVIDUAL);
    }

    public List<LookupTable> getVehicleIdTypes() {
        return this.getByType(ID_TYPE_VEHICLE);
    }
}
