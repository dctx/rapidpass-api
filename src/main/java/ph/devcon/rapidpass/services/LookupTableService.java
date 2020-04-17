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

package ph.devcon.rapidpass.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.entities.RegistrarUser;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.repositories.LookupTableRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ph.devcon.rapidpass.enums.LookupType.APOR;
import static ph.devcon.rapidpass.enums.LookupType.ID_TYPE_INDIVIDUAL;
import static ph.devcon.rapidpass.enums.LookupType.ID_TYPE_VEHICLE;

@Service
@AllArgsConstructor
public class LookupTableService {

    private RegistrarUserRepository registrarUserRepository;
    private LookupTableRepository lookupTableRepository;

//    @Autowired
//    LookupTableService(LookupTableRepository lookupTableRepository) {
//        this.lookupTableRepository = lookupTableRepository;
//    }

    public List<LookupTable> getByType(LookupType type) {
        return this.lookupTableRepository.getAllByLookupTablePKKey(type.toDBType());
    }

    public List<LookupTable> getAporTypes() {
        return this.getByType(APOR);
    }

    public List<LookupTable> getIndividualIdTypes() {
        return this.getByType(ID_TYPE_INDIVIDUAL);
    }

    public List<LookupTable> getVehicleIdTypes() {
        return this.getByType(ID_TYPE_VEHICLE);
    }

    public List<String> getAporTypesForUser(String username) {
        RegistrarUser registrarUser = registrarUserRepository.findByUsername(username);
        List<String> aporTypeList = null;
        if (registrarUser != null) {
            aporTypeList = registrarUser.getRegistrarId().getAporTypeApproverLookupList().stream()
                    .map(a -> a.getAporType())
                    .collect(Collectors.toList());
        }
        return aporTypeList;
    }
}
