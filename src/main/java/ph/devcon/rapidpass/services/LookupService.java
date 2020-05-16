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
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.entities.AporLookup;
import ph.devcon.rapidpass.entities.LookupTable;
import ph.devcon.rapidpass.enums.LookupType;
import ph.devcon.rapidpass.repositories.AporLookupRepository;
import ph.devcon.rapidpass.repositories.LookupTableRepository;
import ph.devcon.rapidpass.repositories.RegistrarUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LookupService {

    private RegistrarUserRepository registrarUserRepository;
    private LookupTableRepository lookupTableRepository;
    private AporLookupRepository aporLookupRepository;

    public List<LookupTable> getByType(LookupType type) {
        return this.lookupTableRepository.getAllByLookupTablePKKey(type.toDBType());
    }

    public List<AporLookup> getAporTypes() {
        return aporLookupRepository.findAll();
    }

    public List<AporLookup> addUpdateAporType(AporLookup data) {
        List<AporLookup> aporSearch = aporLookupRepository.findByAporCode(data.getAporCode());
        Optional<AporLookup> Apor = aporSearch.stream().findFirst();
        AporLookup Apordata = Apor.orElse(null);

        if (Apordata == null) {
            aporLookupRepository.saveAndFlush(data);
            return aporLookupRepository.findAll();
        }

        Apordata.setApprovingAgency(data.getApprovingAgency());
        Apordata.setDescription(data.getDescription());
        aporLookupRepository.saveAndFlush(Apordata);
        //log.info("Edit AporTypes {}", data.getAporCode());
        return aporLookupRepository.findAll();
    }

    public List<AporLookup> deleteAporType(String aporType) {
        List<AporLookup> aporSearch = aporLookupRepository.findByAporCode(aporType);
        Optional<AporLookup> Apor = aporSearch.stream().findFirst();
        AporLookup Apordata = Apor.orElse(null);

        if(Apordata != null) aporLookupRepository.delete(Apordata);
        return aporLookupRepository.findAll();
    }
}
