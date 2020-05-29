package ph.devcon.rapidpass.services;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * This test class holds all of the tests related to the RegistryService, with regard to updating the status of an
 * access pass.
 *
 * For example, this prevents a user from updating the status from "SUSPENDED" to "APPROVED".
 */
@ExtendWith(MockitoExtension.class)
public class RegistryServiceUpdateStatusTest {

    RegistryService instance;

    @Mock
    Authentication mockAuthentication;

    @Mock
    RegistrarRepository mockRegistrarRepository;

    @Mock
    ControlCodeService mockControlCodeService;

    @Mock
    RegistrarUserRepository mockRegistrarUserRepository;

    @Mock
    RegistryRepository mockRegistryRepository;

    @Mock
    RegistrantRepository mockRegistrantRepository;

    @Mock
    AccessPassRepository mockAccessPassRepository;

    @Mock AccessPassNotifierService mockAccessPassNotifierService;

    @Mock ScannerDeviceRepository mockScannerDeviceRepository;

    @Mock
    LookupService lookupService;

    @Mock
    RapidPassEventProducer eventProducer;

    @Mock
    RapidPassRequestProducer requestProducer;

    @Mock
    KeycloakPrincipal mockKeycloakPrincipal;

    @Mock
    KeycloakSecurityContext mockKeyCloakSecurityContext;

    @Mock
    AccessPassEventRepository accessPassEventRepository;

    @BeforeEach
    void setUp() {

        instance = new RegistryService(
                requestProducer,
                eventProducer,
                accessPassEventRepository,
                lookupService,
                mockAccessPassNotifierService,
                mockRegistrarRepository,
                mockRegistryRepository,
                mockControlCodeService,
                mockRegistrantRepository,
                mockAccessPassRepository,
                mockScannerDeviceRepository,
                mockRegistrarUserRepository
        );

        instance.expirationMonth = 5;
        instance.expirationDay = 15;
        instance.expirationYear = 2020;

        instance.isKafkaEnabled =false;
//        OffsetDateTime now = OffsetDateTime.now();
    }

    @Test
    void updateFailIfChangeStatusDeclinedToApproved() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("DECLINED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        update_failIfChangeStatus("DECLINED", "APPROVED");
    }

    @Test
    void updateFailIfChangeStatusDeclinedToPending() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("DECLINED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        when(mockControlCodeService.bindControlCodeForAccessPass(any())).thenReturn(accessPass);

        when(mockAccessPassRepository.saveAndFlush(any())).thenReturn(accessPass);

        update_failIfChangeStatus("DECLINED", "PENDING");
    }

    @Test
    void updateFailIfChangeStatusSuspendedToApproved() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("SUSPENDED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        update_failIfChangeStatus("SUSPENDED", "APPROVED");
    }

    @Test
    void updateFailIfChangeStatusSuspendedToPending() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("SUSPENDED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        when(mockControlCodeService.bindControlCodeForAccessPass(any())).thenReturn(accessPass);

        when(mockAccessPassRepository.saveAndFlush(any())).thenReturn(accessPass);

        update_failIfChangeStatus("SUSPENDED", "PENDING");
    }

    @Test
    void updateFailIfChangeStatusSuspendedToDeclined() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("SUSPENDED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        update_failIfChangeStatus("SUSPENDED", "DECLINED");
    }

    @Test
    void updateFailIfChangeStatusApprovedToPending() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("APPROVED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        when(mockControlCodeService.bindControlCodeForAccessPass(any())).thenReturn(accessPass);

        when(mockAccessPassRepository.saveAndFlush(any())).thenReturn(accessPass);

        update_failIfChangeStatus("APPROVED", "PENDING");
    }

    @Test
    void updateFailIfChangeStatusApprovedToDeclined() {
        AccessPass accessPass;
        accessPass = AccessPass.builder().id(1).referenceID("09171234567").passType("INDIVIDUAL")
                .status("APPROVED")
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567")).thenReturn(
                ImmutableList.of(accessPass)
        );

        update_failIfChangeStatus("APPROVED", "DECLINED");
    }

    /**
     * Reusable piece of testing code, to check whether or not the restrictions on RapidPass updating is ensured.
     *
     * This ensures that the status is only changed when it should be.
     *
     * @param fromStatus The current status of the RapidPass.
     * @param toStatus The target status of the RapidPass.
     */
    void update_failIfChangeStatus(String fromStatus, String toStatus) {
        try {
            instance.updateAccessPass("09171234567",
                    new RapidPassUpdateRequest()
                            .status(RapidPassUpdateRequest.StatusEnum.fromValue(toStatus))
            );

            fail("Expected exception not thrown.");

        }  catch (RegistryService.UpdateAccessPassException e) {
            assertThat(e.getMessage(), containsString(
                    String.format("You are not allowed to change the status of this access pass from %s to %s", fromStatus, toStatus)
            ));
        } catch (Exception e) {
            fail("Unexpected exception thrown.", e);
        }
    }

}
