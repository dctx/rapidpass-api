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

package ph.devcon.rapidpass.utilities.validators.entities.agencyuser;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ph.devcon.rapidpass.entities.Registrar;
import ph.devcon.rapidpass.models.AgencyUser;
import ph.devcon.rapidpass.repositories.RegistrarRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.List;

public abstract class BaseAgencyUserRequestValidator implements Validator {

    private final RegistrarUserRepository registrarUserRepository;
    private final RegistrarRepository registrarRepository;

    private List<Registrar> registrars;


    public BaseAgencyUserRequestValidator(RegistrarUserRepository registrarUserRepository, RegistrarRepository registrarRepository) {
        this.registrarUserRepository = registrarUserRepository;
        this.registrarRepository = registrarRepository;
    }


    private boolean registrarWithShortNameExists(String shortName) {
        if (StringUtils.isEmpty(shortName))
            return false;

        return registrarRepository.findByShortName(shortName) != null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return  (AgencyUser.class.equals(aClass));
    }

    @Override
    public void validate(Object object, Errors errors) {
        AgencyUser agencyUser = object instanceof AgencyUser ? ((AgencyUser) object) : null;
        if (agencyUser != null)
            validateAgencyUser(agencyUser, errors);
    }

    protected void validateAgencyUser(AgencyUser agencyUser, Errors errors) {
        validateRequiredFields(agencyUser, errors);

        if (errors.hasErrors())
            return;

        if (!hasValidRegistrar(agencyUser))
            errors.rejectValue("registrar", "invalid.registrar", "No registrar found with given short name (shortName=" + agencyUser.getRegistrar() + ").");

        if (usernameAlreadyExists(agencyUser))
            errors.rejectValue("username", "duplicate.username", "Username already exists (username=" + agencyUser.getUsername() +  ")");
    }

    protected void validateRequiredFields(AgencyUser agencyUser, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "username", "missing.username", "Missing username.");
        ValidationUtils.rejectIfEmpty(errors, "registrar", "missing.registrar", "Missing registrar.");
   }

    private boolean hasValidRegistrar(AgencyUser agencyUser) {
        return registrarWithShortNameExists(agencyUser.getRegistrar());
    }

    private boolean usernameAlreadyExists(AgencyUser agencyUser) {
        if (StringUtils.isEmpty(agencyUser.getUsername()))
            return false;

        return registrarUserRepository.findByUsername(agencyUser.getUsername()) != null;
    }

    private boolean isBatchUpload(AgencyUser agencyUser, Errors errors) {
        return agencyUser.isBatchUpload();
    }
}
