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

package ph.devcon.rapidpass.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
/*
 * Schema definition for the response of the control codes, on the OpenAPI.yaml.
 *
 * @see {https://gitlab.com/dctx/rapidpass/rapidpass-api/-/blob/develop/src/main/resources/rapidpass-openapi.yaml}
 */
public class ControlCode {
    private String referenceId;

    private String controlCode;

    private String passType;

    public static ControlCode buildFrom(AccessPass accessPass) {

        // TODO: Transform integer control code into string encoding
        String encodedControlCode = String.valueOf(accessPass.getControlCode());

        return ControlCode.builder()
            .passType(accessPass.getPassType())
            .referenceId(accessPass.getReferenceID())
            .controlCode(encodedControlCode)
            .build();
    }
}
