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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.utilities.ControlCodeGenerator;

/**
 * Current implementation of control code as of April 9, 2020.
 */
@Service
public class ControlCodeServiceImpl  implements ControlCodeService {

    /**
     * Secret key used for control code generation.
     *
     * This key changes how the 8 letter control code is generated.
     */
    @Value("${qrmaster.controlkey}")
    private String secretKey;

    private final AccessPassRepository accessPassRepository;

    public ControlCodeServiceImpl(AccessPassRepository accessPassRepository) {
        this.accessPassRepository = accessPassRepository;
    }

    @Override
    public String encode(int id) {
        return ControlCodeGenerator.generate(secretKey, id);
    }


    @Override
    public int decode(String controlCode) {
        if (controlCode == null)
            throw new IllegalArgumentException("Control code must not be null.");

        if (controlCode.length() != 8)
            throw new IllegalArgumentException("Invalid control code length.");
        return ControlCodeGenerator.decode(secretKey, controlCode);
    }

    @Override
    public AccessPass findAccessPassByControlCode(String controlCode) {
        Integer id = decode(controlCode);
        return accessPassRepository.findById(id).orElse(null);
    }

    @Override
    public AccessPass bindControlCodeForAccessPass(AccessPass accessPass) {
        String controlCode = encode(accessPass.getId());
        accessPass.setControlCode(controlCode);
        return accessPass;
    }

}
