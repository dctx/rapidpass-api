package ph.devcon.rapidpass.services.controlcode;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.utilities.ControlCodeGenerator;

/**
 * Current implementation of control code as of April 9, 2020.
 */
@Service
public class ControlCodeServiceImpl  implements ControlCodeService {

    /**
     * Secret key used for control code generation
     */
    @Value("${qrmaster.controlkey:***REMOVED***}")
    private String secretKey = "***REMOVED***";

    private final AccessPassRepository accessPassRepository;

    public ControlCodeServiceImpl(AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }

    @Override
    public String encode(int id) {
        return ControlCodeGenerator.generate(secretKey, id);
    }


    @Override
    public int decode(String controlCode) {
        if (controlCode == null)
            throw new IllegalArgumentException("Control code must not be null.");

        if (controlCode.length() != 8)
            throw new IllegalArgumentException("Invalid control code length.");
        return ControlCodeGenerator.decode(secretKey, controlCode);
    }

    @Override
    public AccessPass findAccessPassByControlCode(String controlCode) {
        Integer id = decode(controlCode);
        return accessPassRepository.findById(id).orElse(null);
    }

    @Override
    public AccessPass bindControlCodeForAccessPass(AccessPass accessPass) {
        if (AccessPassStatus.APPROVED.toString().equals(accessPass.getStatus())) {
            String controlCode = encode(accessPass.getId());
            accessPass.setControlCode(controlCode);
        }
        return accessPass;
    }

}
