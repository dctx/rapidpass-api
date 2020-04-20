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


import java.util.Arrays;
import java.util.List;

/**
 * The Statuses that a RapidPass Request can have.
 */
public enum AccessPassStatus {
    /**
     * Pending request. This is the initial state.
     */
    PENDING,
    /**
     * Approved request.
     */
    APPROVED,
    /**
     * Denied request.
     */
    DECLINED,
    /**
     * Access pass is no longer valid
     */
    SUSPENDED;

    /**
     * List of denied or invalid statuses
     */
    public static List<String> INVALID_STATUSES = Arrays.asList(DECLINED.name(), SUSPENDED.name());

    public static Boolean isValid(String status) {
      for (AccessPassStatus s: AccessPassStatus.values()) {
          if (s.name().equals(status.toUpperCase())) {
              return true;
          }
      }
      return false;
    }
}