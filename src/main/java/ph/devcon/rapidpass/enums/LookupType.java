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

package ph.devcon.rapidpass.enums;

/**
 * Enum representing a type in the LookupTable
 */
public enum LookupType {


    APOR("APOR"),
    ID_TYPE_INDIVIDUAL("IDTYPE-I"),
    ID_TYPE_VEHICLE("IDTYPE-V");

    private final String databaseValue;

    LookupType(final String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public final String toDBType() {
        return databaseValue;
    }

}
