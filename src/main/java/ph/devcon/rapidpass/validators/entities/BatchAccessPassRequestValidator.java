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

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.LookupTableService;

/**
 * Tests whether a{@link RapidPassRequest} or {@link AccessPass} is valid, and ready for creation.
 *
 * <h2>Validation rules</h2>
 * It ensures the following validation rules:
 * 1. ID type is valid (as checked from the database look up tables).
 * 2. APOR type is valid (as checked from the database look up tables).
 * 3. Pass type is valid (INDIVIDUAL or VEHICLE, enum checking).
 * 4. Plate number is a required field if the pass type is vehicle.
 * 5. Create new access pass fails if there is already an existing approved or pending access pass.
 *
 * <h2>Errors</h2>
 * Each error produced is written as a sentence so when combining them, don't use commas. Just use a space to
 * combine the sentences.
 *
 * <h2>Benefits</h2>
 * By generating this class, this validator could be reused with the convenience of memoizing the look up tables,
 * rather than having to repeatedly query the look up tables.
 * <p>
 * This cannot be done with the checking for the existing access passes, as it is possible that the access passes may
 * have changed since the last query.
 */
public class BatchAccessPassRequestValidator extends BaseAccessPassRequestValidator {

    public BatchAccessPassRequestValidator(LookupTableService lookupTableService, AccessPassRepository accessPassRepository) {
        super(lookupTableService, accessPassRepository);
    }

}
