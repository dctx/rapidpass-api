package ph.devcon.rapidpass.validators.entities.agencyuser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.enums.RegistrarUserSource;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseAgencyUserRequestValidatorTest {

    @Mock
    RegistrarUserRepository registrarUserRepository;

    @Mock
    RegistrarRepository registrarRepository;

    private DataBinder binder;

    private BindingResult bindingResult;

    private List<String> errors;

    @Test
    public void newAccessPass_INDIVIDUAL() {

        when(registrarRepository.findByShortName(anyString())).thenReturn(
                Registrar.builder()
                        .shortName("DOH")
                        .id(1)
                        .build()
        );
        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new NewSingleAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        final AgencyUser agencyUser = new AgencyUser();
        agencyUser.setUsername("darrensapalo");
        agencyUser.setPassword("mypassword");
        agencyUser.setEmail("myemail@gmail.com");
        agencyUser.setRegistrar("DOH");

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

        when(registrarRepository.findByShortName(anyString())).thenReturn(
                Registrar.builder()
                        .shortName("DOH")
                        .id(1)
                        .build()
        );
        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new BatchAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        AgencyUser agencyUser = new AgencyUser();
        agencyUser.setUsername("darrensapalo");
        agencyUser.setFirstName("boku-no namae");
        agencyUser.setLastName("boku-no last namae");
        agencyUser.setEmail("myemail@gmail.com");
        agencyUser.setRegistrar("DOH");
        agencyUser.setSource(RegistrarUserSource.BULK.toString());

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
    public void failsIfUsernameAlreadyInUse() {
        when(registrarUserRepository.findByUsername("darrensapalo")).thenReturn(
            RegistrarUser.builder()
                    .username("darrensapalo")
                    .id(1)
                    .build()
        );

        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new NewSingleAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        final AgencyUser agencyUser = new AgencyUser();
        agencyUser.setUsername("darrensapalo");
        agencyUser.setPassword("mypassword");
        agencyUser.setEmail("myemail@gmail.com");
        agencyUser.setRegistrar("DOH");

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItems(containsString("Username already exists")));
    }

    @Test
    public void batchUploadDoesNotRequirePassword() {

        when(registrarRepository.findByShortName(anyString())).thenReturn(
                Registrar.builder()
                        .shortName("DOH")
                        .id(1)
                        .build()
        );

        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new BatchAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        // ---- CASE password is undefined, but source is batch upload  ----
        AgencyUser agencyUser = new AgencyUser();
        agencyUser.setUsername("darrensapalo");
        agencyUser.setFirstName("Darren");
        agencyUser.setLastName("Sapalo");
        agencyUser.setEmail("myemail@gmail.com");
        agencyUser.setRegistrar("DOH");
        agencyUser.setSource(RegistrarUserSource.BULK.name());

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

        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new NewSingleAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        when(registrarRepository.findByShortName(anyString())).thenReturn(null);

        // ---- CASE username is invalid type ----
        AgencyUser agencyUser = new AgencyUser();

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

        // ---- CASE invalid type registrar type ----
        agencyUser = new AgencyUser();
        agencyUser.setUsername("my username");
        agencyUser.setPassword("my password");
        agencyUser.setRegistrar("INVALID_REGISTRAR");

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItems(containsString("No registrar found")));



    }

    @Test
    public void failIfMissingFields_BATCH() {

        // when
        when(registrarRepository.findByShortName(anyString())).thenReturn(null);

        BaseAgencyUserRequestValidator newAccessPassRequestValidator = new BatchAgencyUserRequestValidator(registrarUserRepository, registrarRepository);

        // ---- CASE username is invalid type ----
        AgencyUser agencyUser = new AgencyUser();
        agencyUser.setSource(RegistrarUserSource.BULK.toString());

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

        // ---- Case invalid registrar type ----
        agencyUser = new AgencyUser();
        agencyUser.setSource(RegistrarUserSource.BULK.toString());
        agencyUser.setUsername("my username");
        agencyUser.setPassword("my password");
        agencyUser.setFirstName("my first name");
        agencyUser.setLastName("my last name");
        agencyUser.setRegistrar("INVALID_REGISTRAR");
        agencyUser.setEmail("my email");

        binder = new DataBinder(agencyUser);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItems(containsString("No registrar found")));

    }

}
