package ph.devcon.rapidpass.validators.entities.agency_user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.enums.RegistrarUserSource;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewAgencyUserValidatorTest {

    @Mock
    RegistrarUserRepository registrarUserRepository;

    @Mock
    RegistrarRepository registrarRepository;

    private DataBinder binder;

    private BindingResult bindingResult;

    private List<String> errors;

    @BeforeEach
    public void init() {

        when(registrarRepository.findAll()).thenReturn(
                Collections.singletonList(
                        Registrar.builder()
                                .shortName("DOH")
                                .id(1)
                                .build()
                )
        );
    }

    @Test
    public void newAccessPass_INDIVIDUAL() {

        NewAgencyUserValidator newAccessPassRequestValidator = new NewAgencyUserValidator(registrarUserRepository, registrarRepository);

        AgencyUser agencyUser = AgencyUser.builder()
                .username("darrensapalo")
                .password("mypassword")
                .email("myemail@gmail.com")
                .registrar("DOH")
                .build();

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }

    @Test
    public void newAccessPass_BATCH() {

        NewAgencyUserValidator newAccessPassRequestValidator = new NewAgencyUserValidator(registrarUserRepository, registrarRepository);

        AgencyUser agencyUser = AgencyUser.builder()
                .username("darrensapalo")
                .firstName("boku-no namae")
                .lastName("boku-no last namae")
                .email("myemail@gmail.com")
                .registrar("DOH")
                .source(RegistrarUserSource.BATCH_UPLOAD.toString())
                .build();

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }

    @Test
    public void batchUploadDoesNotRequirePassword() {

        NewAgencyUserValidator newAccessPassRequestValidator = new NewAgencyUserValidator(registrarUserRepository, registrarRepository);

        // ---- CASE password is undefined, but source is batch upload  ----
        AgencyUser agencyUser = AgencyUser.builder()
                .username("darrensapalo")
                .firstName("Darren")
                .lastName("Sapalo")
                .email("myemail@gmail.com")
                .registrar("DOH")
                .source("BATCH_UPLOAD")
                .build();

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }

    @Test
    public void failIfMissingFields_INDIVIDUAL() {

        NewAgencyUserValidator newAccessPassRequestValidator = new NewAgencyUserValidator(registrarUserRepository, registrarRepository);

        // ---- CASE username is invalid type ----
        AgencyUser agencyUser = AgencyUser.builder()
                .build();

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Missing registrar."));
        assertThat(errors, hasItem("Missing password."));
        assertThat(errors, hasItem("Missing username."));
        assertThat(errors, hasItems(containsString("No registrar found")));

    }

    @Test
    public void failIfMissingFields_BATCH() {

        NewAgencyUserValidator newAccessPassRequestValidator = new NewAgencyUserValidator(registrarUserRepository, registrarRepository);

        // ---- CASE username is invalid type ----
        AgencyUser agencyUser = AgencyUser.builder()
                .source(RegistrarUserSource.BATCH_UPLOAD.toString())
                .build();

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Missing username."));
        assertThat(errors, hasItem("Missing first name."));
        assertThat(errors, hasItem("Missing last name."));
        assertThat(errors, hasItem("Missing email."));
        assertThat(errors, hasItems(containsString("No registrar found")));

    }

}
