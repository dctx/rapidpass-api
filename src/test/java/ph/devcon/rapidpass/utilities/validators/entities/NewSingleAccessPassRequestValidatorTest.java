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

package ph.devcon.rapidpass.utilities.validators.entities;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.AporLookup;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupService;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.NewSingleAccessPassRequestValidator;

import java.security.Principal;
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
    LookupService lookupService;

    @Mock
    AccessPassRepository accessPassRepository;

    @Mock
    Principal principal;

    private AccessPass accessPass;
    private RapidPassRequest rapidPassRequest;

    private DataBinder binder;

    private BindingResult bindingResult;

    private List<String> errors;

    @Test
    public void newRapidPassRequest_success() {

        when(lookupService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AporLookup.builder().aporCode("AG").build(),
                        AporLookup.builder().aporCode("BP").build(),
                        AporLookup.builder().aporCode("CA").build()
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

        NewSingleAccessPassRequestValidator newSingleAccessPassRequestValidator =
                new NewSingleAccessPassRequestValidator(lookupService, accessPassRepository, principal);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("IATF")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
                .email("hello@world.com")
                .mobileNumber("09662015319")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newSingleAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        System.out.println(errors);

        assertThat(errors.size(), equalTo(0));


        // ---- CASE No existing access passes ----

        when(accessPassRepository.findAllByReferenceIDOrderByValidToDesc(anyString())).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        // no results
                ))
        );

        newSingleAccessPassRequestValidator =
                new NewSingleAccessPassRequestValidator(lookupService, accessPassRepository, principal);

        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("IATF")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .passType(PassType.INDIVIDUAL)
                .email("hello@world.com")
                .mobileNumber("09662015319")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newSingleAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));
    }


    @Test
    public void failIfExistingAccessPassAlreadyExists() {

        when(lookupService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AporLookup.builder().aporCode("AG").build(),
                        AporLookup.builder().aporCode("BP").build(),
                        AporLookup.builder().aporCode("CA").build()
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

        NewSingleAccessPassRequestValidator newSingleAccessPassRequestValidator =
                new NewSingleAccessPassRequestValidator(lookupService, accessPassRepository, principal);

        // ---- CASE APOR invalid type ----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .email("hello@world.com")
                .plateNumber("ABC 123")
                .mobileNumber("09111234321")
                .passType(PassType.VEHICLE)
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newSingleAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("An existing PENDING/APPROVED RapidPass already exists")));
    }

    @Test
    public void failIfIncorrectMobileNumberFormat() {

    	NewSingleAccessPassRequestValidator newSingleAccessPassRequestValidator =
                new NewSingleAccessPassRequestValidator(lookupService, accessPassRepository, principal);

        // ---- CASE Mobile number has letters----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .email("hello@world.com")
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("a09662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newSingleAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("Invalid mobile input")));


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
                .email("hello@world.com")
                .mobileNumber("659662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(newSingleAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("Invalid mobile input")));
    }
}
