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

package ph.devcon.rapidpass.utilities.validators;

import com.google.common.collect.ImmutableList;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class to quickly perform validation on an object, given the validator and a target object.
 *
 * Throws an illegal argument exception when a validation rule fails.
 * In case that multiple validation rules fail, each default error message is concatenated into a paragraph.
 */

public class StandardDataBindingValidation {
    private final List<Validator> validators;

    /**
     * Supports using only one validator.
     */
    public StandardDataBindingValidation(Validator validators) {
        this.validators = ImmutableList.of(validators);
    }

    /**
     * Supports using only multiple validators.
     */
    public StandardDataBindingValidation(List<Validator> validators) {
        this.validators = validators;
    }

    /**
     * Throws an illegal argument exception when a validation rule fails.
     * In case that multiple validation rules fail, each default error message is concatenated into a paragraph.
     * @throws ReadableValidationException when there are validation errors.
     */
    public void validate(Object target) throws ReadableValidationException {
        DataBinder binder = new DataBinder(target);

        List<String> errors = this.validators.stream()
                .flatMap(validator -> {
                    binder.setValidator(validator);
                    binder.validate();

                    return binder.getBindingResult().getAllErrors()
                            .stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage);
                })
                .collect(Collectors.toList());

        if (errors.size() > 0) {
            String allErrors = String.join(" ", errors);
            throw new ReadableValidationException(allErrors);
        }
    }
}
