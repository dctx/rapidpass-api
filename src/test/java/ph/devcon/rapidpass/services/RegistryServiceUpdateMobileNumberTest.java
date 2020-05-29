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
import ph.devcon.rapidpass.entities.Registrant;
import ph.devcon.rapidpass.kafka.RapidPassEventProducer;
import ph.devcon.rapidpass.kafka.RapidPassRequestProducer;
import ph.devcon.rapidpass.repositories.*;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistryServiceUpdateMobileNumberTest {

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
     * The service should throw an {@link RegistryService.UpdateAccessPassException} if you are trying to change the
     * mobile number of an existing {@link AccessPass} to a mobile number that is currently in use.
     */
    @Test
    void update_failIfMobileNumberInUseAccessPass() {

        String source = "09171234567";
        String target = "09170000000";

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(target)).thenReturn(
                singletonList(AccessPass.builder().referenceID(target).controlCode("that-control-code").build())
        );

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(source)).thenReturn(
                singletonList(AccessPass.builder().referenceID(source).controlCode("my-control-code").build())
        );

        try {
            instance.updateAccessPass(source, new RapidPassUpdateRequest().referenceId(target));
            fail("Expected exception not thrown.");
        } catch (RegistryService.UpdateAccessPassException e) {
            String message = String.format("You are not allowed to change the mobile number of this access pass from %s to %s, because an existing access pass already uses %s.",
                    source, target, target);
            assertThat(e.getMessage(), containsString(message));
        } catch (Exception e) {
            fail("Unexpected exception thrown.", e);
        }
    }

    /**
     * The service should throw an {@link RegistryService.UpdateAccessPassException} if you are trying to change the
     * mobile number of an existing {@link AccessPass} to a mobile number that is currently in use.
     */
    @Test
    void update_failIfMobileNumberInUseRegistrant() {

        String source = "09171234567";
        String target = "09170000000";

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(target)).thenReturn(
                EMPTY_LIST
        );

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(source)).thenReturn(
                singletonList(AccessPass.builder().referenceID(source).controlCode("my-control-code").build())
        );

        when(mockRegistrantRepository.findByReferenceId(target)).thenReturn(
                Registrant.builder()
                        .mobile(target)
                        .referenceId(target)
                        .build()
        );

        try {
            instance.updateAccessPass(source, new RapidPassUpdateRequest().referenceId(target));
            fail("Expected exception not thrown.");
        } catch (RegistryService.UpdateAccessPassException e) {
            String message = String.format("You are not allowed to change the mobile number of this access pass from %s to %s, because an existing registrant already uses %s.",
                    source, target, target);
            assertThat(e.getMessage(), containsString(message));
        } catch (Exception e) {
            fail("Unexpected exception thrown.", e);
        }
    }

    /**
     * The service should succeed in changing the mobile number of an access pass.
     */
    @Test
    void update_success() {

        String source = "09171234567";
        String target = "09170000000";

        final AccessPass accessPass = AccessPass.builder()
                .referenceID(source)
                .controlCode("my-control-code")
                .registrantId(Registrant.builder()
                        .build())
                .build();

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(EMPTY_LIST);

        when(mockAccessPassRepository.findAllByReferenceIDOrderByValidToDesc(source)).thenReturn(
                singletonList(accessPass)
        );

        when(mockAccessPassRepository.saveAndFlush(ArgumentMatchers.any(AccessPass.class))).thenReturn(accessPass);

        try {
            instance.updateAccessPass(source, new RapidPassUpdateRequest().referenceId(target));
            // assertions here
            assertThat(accessPass.getReferenceID(), equalTo(target));
            assertThat(accessPass.getRegistrantId().getMobile(), equalTo(target));
            assertThat(accessPass.getRegistrantId().getReferenceId(), equalTo(target));
            verify(mockAccessPassRepository, times(1)).saveAndFlush(ArgumentMatchers.any(AccessPass.class));
            verify(mockRegistrantRepository, times(1)).saveAndFlush(ArgumentMatchers.any(Registrant.class));
        } catch (Exception e) {
            fail("Unexpected exception thrown.", e);
        }
    }
}
