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

package ph.devcon.rapidpass.services.notifications.templates;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.enums.PassType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Formatter;

@Builder
public class SMSNotificationTemplate implements NotificationTemplate<String> {

    @Value("${notifier.mailFrom:rapidpass-dctx@devcon.ph}")
    private static final String RAPIDPASS_EMAIL = "RapidPass-dctx@devcon.ph";

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private PassType passType;
    
    /**
     * Leave this blank if the {@link ph.devcon.rapidpass.entities.AccessPass} was granted.
     * Supply this value if the {@link ph.devcon.rapidpass.entities.AccessPass} was declined.
     */
    private String reason;

    /**
     * Leave this blank if the {@link ph.devcon.rapidpass.entities.AccessPass} was declined.
     * Supply this value if the {@link ph.devcon.rapidpass.entities.AccessPass} was granted.
     */
    private String controlCode;

    /**
     * This is a required value if the access pass was granted.
     */
    @NotNull
    private String url;

    /**
     * This is a required value if the pass type is vehicle.
     */
    private String vehiclePlateNumber;

    @Override
    public String compose() {
        switch (passType) {
            case INDIVIDUAL:
                return person();
            case VEHICLE:
                return vehicle();
        }
        throw new IllegalArgumentException("Invalid PassType supplied: " + passType);
    }


    private boolean isGranted() {
        return !StringUtils.isEmpty(controlCode);
    }

    private String vehicle() {
        if (StringUtils.isEmpty(vehiclePlateNumber))
            throw new IllegalArgumentException("Invalid vehicle plate number: " + vehiclePlateNumber);

        if (isGranted()) {
            if (StringUtils.isEmpty(url))
                throw new IllegalArgumentException("Invalid URL: " + url);

            String ACCESS_GRANTED = "Your RapidPass has been approved with control code %s for PLATE NO %s! Download your QR here: %s. DO NOT share your QR.";
            return new Formatter().format(ACCESS_GRANTED, controlCode, vehiclePlateNumber, url).toString();
        } else {

        	String ACCESS_DECLINED = "Your RapidPass for the vehicle has been rejected. Please contact your approving agency for further inquiries.";
            return ACCESS_DECLINED;
        }
    }

    private String person() {

        if (isGranted()) {
            if (StringUtils.isEmpty(url))
                throw new IllegalArgumentException("Invalid URL: " + url);

            String ACCESS_GRANTED = "Your RapidPass has been approved with control number %s. Download your QR here: %s. DO NOT share your QR.";
            return new Formatter().format(ACCESS_GRANTED, controlCode, url).toString();
        } else {
        	
            String ACCESS_DECLINED = "Your RapidPass has been rejected. Please contact your approving agency for further inquiries.";
            return new Formatter().format(ACCESS_DECLINED).toString();
        }
    }
}
