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

package ph.devcon.rapidpass.services.notifications;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Qualifier("keycloak")
@Slf4j
@RequiredArgsConstructor
@Setter
public class KeycloakService {

    @NonNull
    private final RestTemplate restTemplate;

    public void registerUser(String imei, String password) {

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
//        params.add("number", formatNumber(message.getTo()));
//        params.add("message", message.getMessage());
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(this.url, request, String.class);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new NotificationException("Error POSTing to semaphore API");
//        }
//
//
//        log.info("response: {}", response.getBody());
//
//
//        log.debug("  SMS msg sent! {}", message.getTo());
    }

    public boolean userExists(String imei) {
        return false;
    }

    public void unregisterUser(String imei) {

    }
}
