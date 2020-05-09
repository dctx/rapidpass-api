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

package ph.devcon.rapidpass.models;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class RapidPassCSVdata {
    @CsvBindByPosition(position = 0)
    private String passType;
    @CsvBindByPosition(position = 1)
    private String aporType;
    @CsvBindByPosition(position = 2)
    private String firstName;
    @CsvBindByPosition(position = 3)
    private String middleName;
    @CsvBindByPosition(position = 4)
    private String lastName;
    @CsvBindByPosition(position = 5)
    private String suffix;
    @CsvBindByPosition(position = 6)
    private String company;
    @CsvBindByPosition(position = 7)
    private String idType;
    @CsvBindByPosition(position = 8)
    private String identifierNumber;
    @CsvBindByPosition(position = 9)
    private String plateNumber;
    @CsvBindByPosition(position = 10)
    private String mobileNumber;
    @CsvBindByPosition(position = 11)
    private String email;
    @CsvBindByPosition(position = 12)
    private String originName;
    @CsvBindByPosition(position = 13)
    private String originStreet;
    @CsvBindByPosition(position = 14)
    private String originCity;
    @CsvBindByPosition(position = 15)
    private String originProvince;
    @CsvBindByPosition(position = 16)
    private String destName;
    @CsvBindByPosition(position = 17)
    private String destStreet;
    @CsvBindByPosition(position = 18)
    private String destCity;
    @CsvBindByPosition(position = 19)
    private String destProvince;
    @CsvBindByPosition(position = 20)
    private String remarks;


}
