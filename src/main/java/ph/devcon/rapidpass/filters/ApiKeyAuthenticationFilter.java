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

package ph.devcon.rapidpass.filters;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link ApiKeyAuthenticationFilter} is a simple filter that checks for the a valid API Key in the request headers.
 *
 * @author jonasespelita@gmail.com
 */
@Component
@Slf4j
@Setter
public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Value("${rapidpass.auth.apiKey.key:secret}")
    private String rapidPassApiKey;

    @Value("${rapidpass.auth.apiKey.header:RP-API-KEY}")
    private String apiKeyHeader;

    @PostConstruct
    void postConstructor() {
        // this filter does not do final authentication. The JWT filter will do that.
        setAuthenticationManager(authentication -> {
            authentication.setAuthenticated(true);
            return authentication;
        });
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        log.debug("Getting API-KEY from request.");
        final String requestApiKey = request.getHeader(apiKeyHeader);

        // check if header api key matches our api key
        if (!rapidPassApiKey.equals(requestApiKey)) {
            log.warn("API Key is not valid!");
            return null;
        }
        log.warn("API Key is authenticated!");
        return requestApiKey;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader(apiKeyHeader);
    }
}
