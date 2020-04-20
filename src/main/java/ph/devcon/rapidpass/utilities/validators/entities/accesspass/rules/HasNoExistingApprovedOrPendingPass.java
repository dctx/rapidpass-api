package ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class HasNoExistingApprovedOrPendingPass implements Validator {

    final AccessPassRepository accessPassRepository;

    public HasNoExistingApprovedOrPendingPass(AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RapidPassRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest request = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;
        if (request == null) return;


        String identifier = PassType.INDIVIDUAL == request.getPassType() ?
                "0" + org.apache.commons.lang3.StringUtils.right(request.getMobileNumber(), 10) :
                request.getPlateNumber();

        if (identifier != null && !hasNoExistingApprovedOrPendingPasses(identifier)) {
            errors.reject("existing.accessPass", String.format("An existing PENDING/APPROVED RapidPass already exists for %s.", identifier));
        }
    }

    /**
     * Checks if there is an existing PENDING/APPROVED RapidPass for referenceId which can be mobile number or
     * plate number.
     * <p>
     * The heaviest query, as this needs direct access to the database every time it needs to check.
     *
     * @param referenceId - The plateNumber if the pass is for a vehicle, or the identifierNumber if an individual.
     * @return true if it is clear to create a new access pass (there is no existing approved or pending passes)
     */
    protected boolean hasNoExistingApprovedOrPendingPasses(String referenceId) {

        referenceId = referenceId.trim();

        OffsetDateTime now = OffsetDateTime.now();

        final List<AccessPass> existingAccessPasses = accessPassRepository.findAllByReferenceIDOrderByValidToDesc(referenceId);

        // get all valid PENDING or APPROVED rapid pass requests for referenceId
        final Optional<AccessPass> existingAccessPass = existingAccessPasses
                .stream()
                .filter(accessPass -> {
                    final AccessPassStatus status = AccessPassStatus.valueOf(accessPass.getStatus().toUpperCase());
                    switch (status) {
                        case PENDING:
                        case APPROVED:
                            return true;
                        default:
                            return false;
                    }
                })
                .findAny();

        return !existingAccessPass.isPresent();
    }
}
