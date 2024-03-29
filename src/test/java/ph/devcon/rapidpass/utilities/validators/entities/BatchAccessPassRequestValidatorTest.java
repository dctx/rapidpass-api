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
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.AporLookup;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupService;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.BatchAccessPassRequestValidator;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BatchAccessPassRequestValidatorTest {

    @Mock
    Authentication mockAuthentication;

    @Mock
    LookupService lookupService;

    @Mock
    AccessPassRepository accessPassRepository;

    @Mock
    Principal mockPrincipal;

    private AccessPass accessPass;
    private RapidPassRequest rapidPassRequest;

    private DataBinder binder;

    private BindingResult bindingResult;

    private List<String> errors;

//    FIXME
//    @Test
    public void newRapidPassRequest_success() {

        // Mocking security to return a keycloak principal
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AG,MS");
        when(mockAuthentication.getPrincipal()).thenReturn(new KeycloakPrincipal<>("test",
                new KeycloakSecurityContext("testtoken", accessToken, "test", new AccessToken())));

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        when(lookupService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AporLookup.builder().aporCode("AG").build(),
                        AporLookup.builder().aporCode("BP").build(),
                        AporLookup.builder().aporCode("CA").build()
                ))
        );

        BatchAccessPassRequestValidator batchAccessPassRequestValidator =
                new BatchAccessPassRequestValidator(lookupService, accessPassRepository, mockPrincipal);

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
                .destCity("Makati")
                .mobileNumber("09111234321")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors.size(), equalTo(0));

        batchAccessPassRequestValidator = new BatchAccessPassRequestValidator(lookupService, accessPassRepository, mockPrincipal);

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
                .passType(PassType.INDIVIDUAL)
                .mobileNumber("09662015319")
                .destCity("Some city")
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

//  FIXME
//    @Test
    public void continueIfExistingAccessPassAlreadyExists() {

        // Mocking security to return a keycloak principal
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AG,MS");
        when(mockAuthentication.getPrincipal()).thenReturn(new KeycloakPrincipal<>("test",
                new KeycloakSecurityContext("testtoken", accessToken, "test", new AccessToken())));

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        when(lookupService.getAporTypes()).thenReturn(
                Collections.unmodifiableList(Lists.newArrayList(
                        AporLookup.builder().aporCode("AG").build(),
                        AporLookup.builder().aporCode("BP").build(),
                        AporLookup.builder().aporCode("CA").build()
                ))
        );

        BatchAccessPassRequestValidator batchAccessPassRequestValidator =
                new BatchAccessPassRequestValidator(lookupService, accessPassRepository, mockPrincipal);

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
                .mobileNumber("+639662015319")
                .destCity("Makati")
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

//    FIXME
//    @Test
    public void failIfIncorrectMobileNumberFormat() {

        // Mocking security to return a keycloak principal
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AG,MS");
        when(mockAuthentication.getPrincipal()).thenReturn(new KeycloakPrincipal<>("test",
                new KeycloakSecurityContext("testtoken", accessToken, "test", new AccessToken())));

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);


        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "test-principal";
            }
        };
        BatchAccessPassRequestValidator batchAccessPassRequestValidator =
                new BatchAccessPassRequestValidator(lookupService, accessPassRepository, principal);

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
                .destCity("Makati")
                .mobileNumber("a09662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

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
                .mobileNumber("+659662006888")
                .build();

        binder = new DataBinder(rapidPassRequest);
        binder.setValidator(batchAccessPassRequestValidator);

        binder.validate();

        bindingResult = binder.getBindingResult();

        errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        assertThat(errors, hasItem(containsString("Invalid mobile input")));
    }

//  FIXME
//    @Test
    public void requiredDestinationCity() {
        // Mocking security to return a keycloak principal
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AG,MS");
        when(mockAuthentication.getPrincipal()).thenReturn(new KeycloakPrincipal<>("test",
                new KeycloakSecurityContext("testtoken", accessToken, "test", new AccessToken())));

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        BatchAccessPassRequestValidator batchAccessPassRequestValidator =
                new BatchAccessPassRequestValidator(lookupService, accessPassRepository, mockPrincipal);

        // ---- CASE Mobile number has letters----
        rapidPassRequest = RapidPassRequest.builder()
                .aporType("AG")
                .idType("PLT")
                .identifierNumber("ABC 123")
                .firstName("Juan")
                .lastName("dela Cruz")
                .originStreet("Abbey Road")
                .plateNumber("ABC 123")
                .destCity(null)
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

        assertThat(errors, hasItem(containsString("Missing Destination City")));

    }
}
