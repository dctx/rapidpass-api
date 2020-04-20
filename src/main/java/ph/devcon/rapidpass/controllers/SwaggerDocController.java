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

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manually expose swagger doc
 */
@RestController
@RequestMapping("/spec")
@Slf4j
public class SwaggerDocController {

    @GetMapping(value = "", produces = "application/yml")
    public byte[] getSwaggerSpec() {
        InputStream in = getClass().getResourceAsStream("/rapidpass-openapi.yaml");
        try {
            return StreamUtils.copyToByteArray(in);
        } catch (IOException e) {
//            log.error(e.getMessage());
        }
        return null;
    }
}
