/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 * See the License for the specific language governing permissions and limitations under the License.
 */

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
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;
import ph.devcon.rapidpass.validators.entities.accesspass.BatchAccessPassRequestValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BatchAccessPassRequestValidatorTest {

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
    public void newRapidPassRequest_success() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(lookupTableService.getIndividualIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "LTO")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "COM")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "NBI"))
                ))
        );

        when(lookupTableService.getVehicleIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "PLT")),
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "CND"))
                ))
        );

        // ---- CASE already has one existing access pass, but it is declined ----

//        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
//                Collections.unmodifiableList(Lists.newArrayList(
//                        AccessPass.builder()
//                                .referenceID("DEF 456")
//                                .status(AccessPassStatus.DECLINED.toString())
//                                .aporType("AG")
//                                .plateNumber("DEF 456")
//                                .build()
//                ))
//        );

        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("09111234321")
                .passType(PassType.VEHICLE)
                .mobileNumber("09662015319")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));


        // ---- CASE No existing access passes ----

//        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
//                Collections.unmodifiableList(Lists.newArrayList(
//                        // no results
//                ))
//        );

        batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .identifierNumber("ABC 123")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .mobileNumber("09662015319")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }


    @Test
    public void failIfMissingIdType() {

        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE APOR invalid type ----
        
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("09111234321")
                .idType("SOME INVALID ID TYPE")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Invalid ID Type."));


        // ---- CASE APOR is null  ----

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("09111234321")
        		.passType(PassType.INDIVIDUAL)
                .idType(null)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Missing ID Type."));

        System.out.println(bindingResult);
    }

    @Test
    public void failIfMissingPlateNumberIfVehicle() {

        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE pass type is vehicle, and plate number is missing ----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("09111234321")
                .plateNumber(null)
                .passType(PassType.VEHICLE)
                .build();


        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem("Missing Plate Number."));

        // ---- CASE pass type is individual, and plate number is missing  ----

        rapidPassRequest = RapidPassRequest.builder()
                .passType(PassType.INDIVIDUAL)
                .plateNumber(null)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, not(hasItem("Missing plate number.")));

        System.out.println(bindingResult);
    }

    @Test
    public void continueIfExistingAccessPassAlreadyExists() {

        when(lookupTableService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("APOR", "AG")),
                        new LookupTable(new LookupTablePK("APOR", "BP")),
                        new LookupTable(new LookupTablePK("APOR", "CA"))
                ))
        );

        when(lookupTableService.getIndividualIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "LTO")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "COM")),
                        new LookupTable(new LookupTablePK("IDTYPE-IND", "NBI"))
                ))
        );

        when(lookupTableService.getVehicleIdTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "PLT")),
                        new LookupTable(new LookupTablePK("IDTYPE-VHC", "CND"))
                ))
        );

//        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
//                Collections.unmodifiableList(Lists.newArrayList(
//                        AccessPass.builder()
//                                .referenceID("ABC 123")
//                                .aporType("AG")
//                                .plateNumber("ABC 123")
//                                .status(AccessPassStatus.PENDING.toString())
//                                .build()
//                ))
//        );

        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE APOR invalid type ----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.VEHICLE)
                .mobileNumber("+639662015319")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, is(empty()));
    }
    
    @Test
    public void failIfIncorrectMobileNumberFormat() {
   
        
        BatchAccessPassRequestValidator batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupTableService, accessPassRepository);

        // ---- CASE Mobile number has letters----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
        		.mobileNumber("a09662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("Incorrect mobile number format")));
        
        
        // ---- CASE Mobile number can only use Philippine numbers ----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
        		.mobileNumber("+659662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("Incorrect mobile number format")));
    }
}
