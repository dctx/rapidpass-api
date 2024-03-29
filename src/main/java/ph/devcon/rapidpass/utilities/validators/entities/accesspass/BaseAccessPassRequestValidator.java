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

package ph.devcon.rapidpass.utilities.validators.entities.accesspass;

import com.google.common.collect.ImmutableList;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.entities.AporLookup;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupService;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules.IsValidAporType;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules.IsValidMobileNumber;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules.IsValidPassType;
import ph.devcon.rapidpass.utilities.validators.entities.accesspass.rules.RequiredField;

import java.security.Principal;
import java.util.List;

public abstract class BaseAccessPassRequestValidator implements Validator {

    final AccessPassRepository accessPassRepository;
    final LookupService lookupService;
    final Principal principal;

    protected List<AporLookup> aporTypes;

    public BaseAccessPassRequestValidator(LookupService lookupService, AccessPassRepository accessPassRepository,
                                          Principal principal) {
        this.lookupService = lookupService;
        this.accessPassRepository = accessPassRepository;
        this.principal = principal;

        aporTypes = lookupService.getAporTypes();
    }

    /**
     * This validator works for both {@link RapidPassRequest} and {@link AccessPass}.
     *
     * @return true if the class is supported.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        if (RapidPassRequest.class.equals(aClass))
            return true;
        return AccessPass.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RapidPassRequest rapidPassRequest = object instanceof RapidPassRequest ? ((RapidPassRequest) object) : null;
        if (rapidPassRequest != null)
            validateRapidPassRequest(rapidPassRequest, errors);
    }

    protected void validateRequiredFields(RapidPassRequest request, Errors errors) {

        ImmutableList<Validator> validations = ImmutableList.of(
                new RequiredField("passType", "missing.passType", "Missing Pass Type."),
                new RequiredField("aporType", "missing.aporType", "Missing APOR Type."),
                new RequiredField("idType", "missing.idType", "Missing ID Type."),
                new RequiredField("firstName", "missing.firstName", "Missing First Name."),
                new RequiredField("lastName", "missing.lastName", "Missing Last Name.")
        );

        validations.forEach(validator -> validator.validate(request, errors));

    }

    protected void validateRapidPassRequest(RapidPassRequest request, Errors errors) {

        validateRequiredFields(request, errors);

        // Don't do db related queries if there were already some non-db related validation errors that failed.
        if (errors.hasErrors())
            return;

        ImmutableList<Validator> validations = ImmutableList.of(
            new IsValidAporType(this.aporTypes),
            new IsValidPassType(),
            new IsValidMobileNumber()
        );

        validations.forEach(validator -> validator.validate(request, errors));

    }
}

