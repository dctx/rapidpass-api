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

package ph.devcon.rapidpass.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.api.models.RegistrarUserChangePasswordRequest;
import ph.devcon.rapidpass.exceptions.AccountLockedException;
import ph.devcon.rapidpass.models.AgencyAuth;
import ph.devcon.rapidpass.models.Login;
import ph.devcon.rapidpass.models.UserActivationRequest;
import ph.devcon.rapidpass.services.LookupTableService;
import ph.devcon.rapidpass.utilities.JwtGenerator;
import ph.devcon.rapidpass.utilities.KeycloakUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final LookupTableService lookupTableService;

    /***
     * @deprecated use {@code /users/apor-types} instead
     * @param userName
     * @return
     */
    @Deprecated
    @GetMapping("/{userName}/apor-types")
    public final ResponseEntity<List<String>> getAporTypesByUser(@PathVariable String userName) {
        List<String> aporTypesForUser = lookupTableService.getAporTypesForUser(userName);
        if (aporTypesForUser == null || aporTypesForUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(aporTypesForUser);
        }
    }

    /**
     * Endpoint to retrieve authorized apor types for a user. Expects an access token in authorization header.
     *
     * @return 200 - JSON list of apor types, 403 - no valid authorization header
     */
    @GetMapping("/apor-types")
    public ResponseEntity<?> getAuthorizedAporTypes() {
        final Map<String, String> attributes = KeycloakUtils.getAttributes();
        log.debug("found attributes: {}", attributes);
        final String aporTypes = attributes.get("aportypes");
        return ResponseEntity.ok(
                StringUtils.isEmpty(aporTypes)
                        ? new ArrayList<>()
                        : aporTypes
                        .split(","));
    }

}
