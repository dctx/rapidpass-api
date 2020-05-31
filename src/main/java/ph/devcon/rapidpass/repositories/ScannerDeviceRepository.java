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

import javax.persistence.criteria.Expression;
import java.util.List;

public interface ScannerDeviceRepository extends
        JpaRepository<ScannerDevice, Integer>,
        JpaSpecificationExecutor<ScannerDevice> {

    List<ScannerDevice> findAll();

    ScannerDevice findById(String id);

    ScannerDevice findByUniqueDeviceId(String deviceId);

    List<ScannerDevice> findByImei(String imei);

    class ScannerDeviceSpecs {
        public static Specification<ScannerDevice> byBrand(String brand) {

            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isEmpty(brand)) return null;

                Expression<String> lowerBrand = criteriaBuilder.function("lower", String.class, root.get("brand"));

                return criteriaBuilder.like(lowerBrand, "%" + StringUtils.lowerCase(brand) + "%");
            };
        }

        public static Specification<ScannerDevice> byMobileNumber(String mobileNumber) {
            return (root, query, criteriaBuilder) ->
                    StringUtils.isEmpty(mobileNumber) ? null :
                            criteriaBuilder.like(root.get("mobileNumber").as(String.class), "%" + mobileNumber + "%");
        }

        public static Specification<ScannerDevice> byModel(String model) {
            return (root, query, criteriaBuilder) -> {
                    if (StringUtils.isEmpty(model)) return null;

                    Expression<String> lowerBrand = criteriaBuilder.function("lower", String.class, root.get("model"));

                    return criteriaBuilder.like(lowerBrand, "%" + StringUtils.lowerCase(model) + "%");
                };
        }

        public static Specification<ScannerDevice> byIMEI(String imei) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isEmpty(imei)) return null;

                Expression<String> lowerBrand = criteriaBuilder.function("lower", String.class, root.get("imei"));

                return criteriaBuilder.like(lowerBrand, "%" + StringUtils.lowerCase(imei) + "%");
            };
        }

        public static Specification<ScannerDevice> byDeviceId(String deviceId) {
            return (root, query, criteriaBuilder) -> {
                if (StringUtils.isEmpty(deviceId)) return null;

                Expression<String> lowerBrand = criteriaBuilder.function("lower", String.class, root.get("uniqueDeviceId"));

                return criteriaBuilder.like(lowerBrand, "%" + StringUtils.lowerCase(deviceId) + "%");
            };
        }
    }
}
