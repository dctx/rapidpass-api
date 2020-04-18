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

package ph.devcon.rapidpass.services.controlcode;

import ph.devcon.rapidpass.entities.AccessPass;

/**
 * Defines the interface for a control code service.
 */
public interface ControlCodeService {
    /**
     * Encodes an integer into an 8-letter control code string.
     *
     * @param id The id of the {@link ph.devcon.rapidpass.entities.AccessPass}.
     * @return An 8-letter control code string.
     */
    String encode(int id);

    /**
     * Decodes an 8-letter control code string into an integer.
     *
     * @param controlCode An 8-letter control code string.
     * @return The id of the {@link ph.devcon.rapidpass.entities.AccessPass}.
     */
    int decode(String controlCode);

    /**
     * Control codes can be bound to the access pass only if they are already approved.
     *
     * <p>Note that this will not bind a control code value if the access pass is not yet approved.</p>
     *
     * @param accessPass The access pass to bind control code value.
     * @return The same access pass.
     */
    AccessPass bindControlCodeForAccessPass(AccessPass accessPass);

    /**
     * Returns an access pass based on the specified control code.
     * @param controlCode The 8 letter control code.
     * @return An {@link AccessPass} or a null, if none was found.
     */
    AccessPass findAccessPassByControlCode(String controlCode);
}
