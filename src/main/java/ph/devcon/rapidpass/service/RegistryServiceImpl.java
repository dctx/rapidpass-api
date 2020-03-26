package ph.devcon.rapidpass.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.model.RapidPassRequest;

import static ph.devcon.rapidpass.model.RapidPassRequest.AccessType.O;
import static ph.devcon.rapidpass.model.RapidPassRequest.RequestType.INDIVIDUAL;

/**
 * Implementation for {@link RegistryService}.
 */
@Service
@Slf4j
public class RegistryServiceImpl implements RegistryService {
    @Override
    public void createPassRequest(RapidPassRequest rapidPassRequest) {
        log.debug("createRequestPass called for {}!", rapidPassRequest);

        // TODO: Persistence
    }

    @Override
    public RapidPassRequest getPassRequest(String referenceId) {

        // TODO: Get from Persistence

        // returns a stub
        return RapidPassRequest.builder()
                .passType(INDIVIDUAL)
                .name("Jonas Espelita")
                .mobileNumber("string")
                .email("jonas.was.here@gmail.com")
                .destAddress("Somewhere in the PH")
                .company("DEVCON")
                .accessType(O)
                .remarks("This is a test")
                .build();
    }
}
