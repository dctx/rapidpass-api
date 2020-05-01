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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.models.QueryFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AccessPassSpecifications {

    public static Specification<AccessPass> byAporTypes(List<String> aporTypes) {
        return ((root, query, criteriaBuilder) -> {
            if (aporTypes == null || aporTypes.isEmpty()) {
                return null;
            }
            CriteriaBuilder.In<Object> inClause = criteriaBuilder.in(root.get("aporType"));
            for (String aporType : aporTypes) {
                inClause.value(aporType);
            }
            return inClause;
        });
    }

    public static Specification<AccessPass> byPassType(String passType) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(passType))
                return null;
            return criteriaBuilder.equal(root.get("passType"), passType);
        }));
    }

    public static Specification<AccessPass> byReferenceId(String referenceId) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(referenceId))
                return null;
            return criteriaBuilder.equal(root.get("referenceID"), referenceId);
        }));
    }

    public static Specification<AccessPass> byStatus(String status) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(status))
                return null;
            return criteriaBuilder.equal(root.get("status"), status);
        }));
    }

    public static Specification<AccessPass> byPlateNumber(String plateNumber) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(plateNumber))
                return null;
            return criteriaBuilder.equal(root.get("plateNumber"), plateNumber);
        }));
    }

    public static Specification<AccessPass> byCompany(String company) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(company))
                return null;

            Expression<String> lowerCompany = criteriaBuilder.function("lower", String.class, root.get("name"));

            return criteriaBuilder.like(lowerCompany, "%"+StringUtils.lowerCase(company)+"%");
        }));
    }

    public static Specification<AccessPass> bySearch(String search) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(search))
                return null;

            Expression<String> lowerName = criteriaBuilder.function("lower", String.class, root.get("name"));
            Expression<String> lowerCompany = criteriaBuilder.function("lower", String.class, root.get("company"));

            return criteriaBuilder.or(
                    criteriaBuilder.like(lowerCompany, "%"+StringUtils.lowerCase(search)+"%"),
                    criteriaBuilder.like(lowerName, "%"+StringUtils.lowerCase(search).replaceAll("\\s", "%")+"%"),
                    criteriaBuilder.like(root.get("referenceID"), String.format("%%%s%%", search))
            );
        }));
    }

    public static Specification<AccessPass> byName(String name) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(name))
                return null;

            Expression<String> lowerName = criteriaBuilder.function("lower", String.class, root.get("name"));

            return criteriaBuilder.like(lowerName, "%"+StringUtils.lowerCase(name)+"%");
        }));
    }

    public static Specification<AccessPass> bySource(String source) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isBlank(source))
                return null;
            return criteriaBuilder.equal(root.get("source"), source);
        }));
    }

    // FIXME not working yet
    public static Specification<AccessPass> byExample(QueryFilter queryFilter) {
        return (((root, criteriaQuery, criteriaBuilder) -> {
            Predicate specs = null;
            List<Predicate> predicateList = new ArrayList<>();
            for (Field field : queryFilter.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                if (!fieldName.equalsIgnoreCase("aporType")) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(queryFilter);
                        if (value != null) {
                            specs = criteriaBuilder.equal(root.get(fieldName), value);
                            criteriaQuery.where(specs);
                        }
                    } catch (IllegalArgumentException e) {
                        // noop, ignore and move on
                    } catch (IllegalAccessException e) {
                        log.warn("Unable to get value for field: {}", fieldName);
                    }
                }
            }
            return specs;
        }));
    }
}
