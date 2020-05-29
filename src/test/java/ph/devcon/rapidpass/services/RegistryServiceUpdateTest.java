package ph.devcon.rapidpass.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ph.devcon.rapidpass.api.models.RapidPassUpdateRequest;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.RegistryService.UpdateAccessPassException;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * This test class holds all of the tests related to RegistryService with regard to updates on the access pass.
 */
@ExtendWith(MockitoExtension.class)
public class RegistryServiceUpdateTest {

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

    /**
     * This tests whether or not a RapidPass can be successfully approved by the {@link RegistryService}.
     */
    @Test
    void update_approveRapidPass() {
        final AccessPass approvedAccessPass = AccessPass.builder()
                .id(123456)
                .referenceID("ref-id")
                .status("APPROVED")
                .passType("INDIVIDUAL")
                .build();

        final AccessPass pendingAccessPass = AccessPass.builder()
                .status("PENDING")
                .id(123456)
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("ref-id"))
                .thenReturn(singletonList(pendingAccessPass));
        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any(AccessPass.class))).thenReturn(approvedAccessPass);

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.APPROVED);
        approveRequest.setRemarks(null);
        try {
            final RapidPass approved = instance.updateAccessPass(
                    "ref-id",
                    approveRequest
            );

            assertThat(approved, is(notNullValue()));
            assertThat(approved.getStatus(), is("APPROVED"));

        }catch (UpdateAccessPassException e) {
            fail("Unexpected exception occurred.", e);
        }

        verify(mockAccessPassRepository, times(2)).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }

    @Test
    void update_declineRapidPass() throws UpdateAccessPassException {
        final AccessPass approvedAccessPass = AccessPass.builder().id(1).status("DECLINED").passType("INDIVIDUAL")
                .referenceID("09171234567")
                .build();

        final AccessPass pendingAccessPass = AccessPass.builder().id(1)
                .status("PENDING")
                .build();


        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc("09171234567"))
                .thenReturn(singletonList(pendingAccessPass));
        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any(AccessPass.class))).thenReturn(approvedAccessPass);

        RapidPassUpdateRequest approveRequest = new RapidPassUpdateRequest();
        approveRequest.setStatus(RapidPassUpdateRequest.StatusEnum.DECLINED);
        approveRequest.setRemarks("Some reason here");

        final RapidPass approved = instance.updateAccessPass("09171234567", approveRequest);

        assertThat(approved, is(notNullValue()));
        assertThat(approved.getStatus(), is("DECLINED"));

        verify(mockAccessPassRepository, times(2)).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
    }


    /**
     * The service should throw an {@link UpdateAccessPassException} if the RapidPass being updated does not exist.
     */
    @Test
    void update_failIfNonExistent() {

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(EMPTY_LIST);

        try {
            instance.updateAccessPass("09171234567", new RapidPassUpdateRequest().referenceId("09171234567"));
            fail("Expected exception not thrown.");
        } catch (UpdateAccessPassException e) {
            assertThat(e.getMessage(), containsString("There was no access pass found with reference ID"));
        } catch (Exception e) {
            fail("Unexpected exception thrown.");
        }
    }


}
