package ph.devcon.rapidpass.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.RequestStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.repositories.RegistrantRepository;
import ph.devcon.rapidpass.repositories.RegistryRepository;

@Component
@Slf4j
public class DashboardService {

    private AccessPassRepository accessPassRepository;

    @Autowired // this should be mapped to a service
    public DashboardService(RegistryRepository registryRepository, RegistrantRepository registrantRepository, AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }


    /**
     * After updating the target {@link AccessPass}, this returns a {@link RapidPass} whose status is granted.
     * @param referenceId The reference id of the {@link AccessPass} you are retrieving.
     * @param status The status to apply
     * @return Data stored on the database
     */
    private RapidPass updateStatus(String referenceId, RequestStatus status) throws RegistryService.UpdateAccessPassException {
        AccessPass accessPass = accessPassRepository.findByReferenceId(referenceId);

        String currentStatus = accessPass.getStatus();

        boolean isPending = RequestStatus.PENDING.toString().equals(currentStatus);

        if (!isPending) {
            throw new RegistryService.UpdateAccessPassException("An access pass can only be updated if it is pending. Afterwards, it can only be revoked.");
        }

        accessPass.setStatus(status.toString());

        // TODO: We need to verify that only the authorized people to modify this pass are allowed.
        // E.g. approvers, or the owner of this pass. People should not be able to re-associate an existing pass from one registrant to another.
        // accessPass.setRegistrantId();

        // TODO: This update operation doesn't update the access pass' validity. we used a constant value for now.

        AccessPass savedAccessPass = accessPassRepository.saveAndFlush(accessPass);
        return RapidPass.buildFrom(savedAccessPass);
    }

    public RapidPass grant(String referenceId) throws  RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, RequestStatus.APPROVED);
    }

    public RapidPass decline(String referenceId) throws  RegistryService.UpdateAccessPassException {
        return this.updateStatus(referenceId, RequestStatus.DENIED);
    }
}
