package ph.devcon.rapidpass.validators.entities;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.entities.LookupTablePK;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewSingleAccessPassRequestValidatorTest {

    @Mock
    LookupTableService lookupTableService;

    @Mock
    AccessPassRepository accessPassRepository;

    private AccessPass accessPass;
    private RapidPassRequest rapidPassRequest;

    private DataBinder binder;

    private BindingResult bindingResult;

    private List<String> errors;

    @Test
    public void newAccessPass_success() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );


        // ---- CASE already has one existing access pass, but it is declined ----

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AccessPass.builder()
                                .referenceID("DEF 456")
                                .status(AccessPassStatus.DECLINED.toString())
                                .aporType("AG")
                                .plateNumber("DEF 456")
                                .build()
                ))
        );

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        accessPass = AccessPass.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE.toString())
                .referenceID("101")
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));


        // ---- CASE No existing access passes ----

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                    // no results
                ))
        );

        newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        accessPass = AccessPass.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE.toString())
                .referenceID("101")
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }

    @Test
    public void newRapidPassRequest_success() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        // ---- CASE already has one existing access pass, but it is declined ----

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AccessPass.builder()
                                .referenceID("DEF 456")
                                .status(AccessPassStatus.DECLINED.toString())
                                .aporType("AG")
                                .plateNumber("DEF 456")
                                .build()
                ))
        );

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));


        // ---- CASE No existing access passes ----

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        // no results
                ))
        );

        newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }

    @Test
    public void failIfMissingPassType() {

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE pass type is invalid type ----
        accessPass = AccessPass.builder()
                .passType("SOME INVALID PASS TYPE")
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Invalid Pass Type."));


        // ---- CASE pass type is missing is null  ----

        accessPass = AccessPass.builder()
                .passType(null)
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Invalid Pass Type."));

        System.out.println(bindingResult);
    }

    @Test
    public void failIfMissingPlateNumberIfVehicle() {

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE pass type is vehicle, and plate number is missing ----
        accessPass = AccessPass.builder()
                .plateNumber(null)
                .passType(PassType.VEHICLE.toString())
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Missing plate number."));

        // ---- CASE pass type is individual, and plate number is missing  ----

        accessPass = AccessPass.builder()
                .passType(PassType.INDIVIDUAL.toString())
                .plateNumber(null)
                .build();

        binder = new DataBinder(accessPass);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, not(hasItem("Missing plate number.")));

        System.out.println(bindingResult);
    }

    @Test
    public void failIfExistingAccessPassAlreadyExists() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AccessPass.builder()
                                .referenceID("ABC 123")
                                .aporType("AG")
                                .plateNumber("ABC 123")
                                .status(AccessPassStatus.PENDING.toString())
                                .build()
                ))
        );

        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE APOR invalid type ----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(String.format("An existing PENDING/APPROVED RapidPass already exists for %s", rapidPassRequest.getPlateNumber())));
    }
    
    @Test
    public void failIfIncorrectMobileNumberFormat() {
   
        
        NewAccessPassRequestValidator newAccessPassRequestValidator = new NewAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE Mobile number has letters----
        rapidPassRequest = RapidPassRequest.builder()
                .mobileNumber("a09662006888")
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Incorrect mobile number format"));
        
        
        // ---- CASE Mobile number has more than 11 characters but doesnt start with +639----
        rapidPassRequest = RapidPassRequest.builder()
                .mobileNumber("639662006888")
                .aporType("AG")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Incorrect mobile number format"));
    }
}
