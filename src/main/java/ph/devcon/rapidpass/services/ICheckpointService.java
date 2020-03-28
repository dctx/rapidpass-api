package ph.devcon.rapidpass.services;

import ph.devcon.rapidpass.entities.AccessPass;

public interface ICheckpointService {

    AccessPass getAccessPassByControlCode(String controlCode);

}
