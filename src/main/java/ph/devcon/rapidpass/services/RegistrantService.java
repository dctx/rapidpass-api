package ph.devcon.rapidpass.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.repositories.RegistrantRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrantService {
    private final RegistrantRepository registrantRepository;


}
