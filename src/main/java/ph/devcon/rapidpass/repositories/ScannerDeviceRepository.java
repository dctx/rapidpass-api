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

package ph.devcon.rapidpass.repositories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ph.devcon.rapidpass.entities.ScannerDevice;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ScannerDeviceRepository extends
        JpaRepository<ScannerDevice, Integer>,
        JpaSpecificationExecutor<ScannerDevice> {

    List<ScannerDevice> findAll();

    ScannerDevice findById(String id);

    ScannerDevice findByUniqueDeviceId(String imei);

    class ScannerDeviceSpecs {
        public static Specification<ScannerDevice> byBrand(String brand) {
            return (root, query, criteriaBuilder) ->
                    StringUtils.isEmpty(brand) ? null :
                            criteriaBuilder.like(root.get("brand").as(String.class), "%" + brand + "%");
        }

        @NotNull
        public static Specification<ScannerDevice> byMobileNumber(String mobileNumber) {
            return (root, query, criteriaBuilder) ->
                    StringUtils.isEmpty(mobileNumber) ? null :
                            criteriaBuilder.like(root.get("mobileNumber").as(String.class), "%" + mobileNumber + "%");
        }

        @NotNull
        public static Specification<ScannerDevice> byModel(String model) {
            return (root, query, criteriaBuilder) ->
                    StringUtils.isEmpty(model) ? null :
                            criteriaBuilder.like(root.get("model").as(String.class), "%" + model + "%");
        }

        @NotNull
        public static Specification<ScannerDevice> byId(String id) {
            return (root, query, criteriaBuilder) ->
                    StringUtils.isEmpty(id) ? null :
                            criteriaBuilder.like(root.get("uniqueDeviceId").as(String.class), "%" + id + "%");
        }
    }
}
