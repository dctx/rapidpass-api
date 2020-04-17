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

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private BuildProperties buildProperties;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index() {
        return "redirect:swagger-ui.html";
    }

    /**
     * Exposes build information.
     *
     * @return build info in json fmt
     */
    @GetMapping("/version")
    public HttpEntity<Map<String, String>> getVersion() {
        if (buildProperties == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(ImmutableMap.of("version",
                String.format("%s.%d", buildProperties.getVersion(), buildProperties.getTime().getEpochSecond())));
    }
}
