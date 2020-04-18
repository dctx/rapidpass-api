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

package ph.devcon.rapidpass.validators.entities.accesspass;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;
import ph.devcon.rapidpass.validators.entities.accesspass.rules.HasNoExistingApprovedOrPendingPass;

/**
 * Subset of {@link BatchAccessPassRequestValidator}, that doesn't do id type checking.
 *
 * The id type checking will be handled by the front end for now (so they won't cram too much work).
 */
public class NewSingleAccessPassRequestValidator extends BaseAccessPassRequestValidator {

    public NewSingleAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
        super(lookupTableService, accessPassRepository);
    }

    protected void validateRequiredFields(RapidPassRequest request, Errors errors) {
        super.validateRequiredFields(request, errors);

        ValidationUtils.rejectIfEmpty(errors, "email", "missing.email", "Missing Email.");

        if (errors.hasErrors())
            return;

        HasNoExistingApprovedOrPendingPass hasNoExistingApprovedOrPendingPass = new HasNoExistingApprovedOrPendingPass(this.accessPassRepository);
        hasNoExistingApprovedOrPendingPass.validate(request, errors);
    }

}
