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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.devcon.rapidpass.services.LookupService;
import ph.devcon.rapidpass.utilities.KeycloakUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserRestController {

    private final LookupService lookupService;

    /**
     * Retrieves the APOR types of the specified user.
     * @see #getAuthorizedAporTypes(Principal)
     * @deprecated
     */
    @Deprecated
    @GetMapping("/{userName}/apor-types")
    public final ResponseEntity<?> getAporTypesByUser(@PathVariable String userName, Principal principal) {
        // APOR types should be retrieved from Keycloak.
        log.warn("This GET /{username}/apor-types has been deprecated, but is still being called.");
        return this.getAuthorizedAporTypes(principal);
    }

    /**
     * Endpoint to retrieve authorized apor types for the currently logged in user.
     *
     * This expects an access token in authorization header.
     *
     * @return 200 - JSON list of apor types, 403 - no valid authorization header
     */
    @GetMapping("/apor-types")
    public ResponseEntity<?> getAuthorizedAporTypes(Principal principal) {
//        final Map<String, String> attributes = KeycloakUtils.getAttributes();
        final Map<String, String> attributes = KeycloakUtils.getOtherClaims(principal);
        log.debug("found attributes: {}", attributes);
        final String aporTypes = attributes.get("aportypes");
        return ResponseEntity.ok(
                StringUtils.isEmpty(aporTypes)
                        ? new ArrayList<>()
                        : aporTypes
                        .split(","));
    }

}
