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


    private String shortenedName() {
        String shortenedName = name;
        return shortenedName;
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

            // 150 characters without bound variables
            String ACCESS_GRANTED = "Hi %s! Your RapidPass has been approved with control number %s. Download your QR code thru this link: %s";
            return new Formatter().format(ACCESS_GRANTED, shortenedName(), vehiclePlateNumber, controlCode, url).toString();
        } else {
            // 129 characters failed, including rapidpass email, without bound user name
            String ACCESS_DECLINED = "Hi, %s. Your entry has been rejected due to incomplete field/s. Please register individually via RapidPass.ph to get your QR code.";
            return new Formatter().format(ACCESS_DECLINED, shortenedName(), RAPIDPASS_EMAIL).toString();
        }
    }

    private String person() {

        if (isGranted()) {
            if (StringUtils.isEmpty(url))
                throw new IllegalArgumentException("Invalid URL: " + url);

            // 150 characters without bound variables
            String ACCESS_GRANTED = "Hi %s! Your RapidPass has been approved with control number %s. Download your QR code thru this link: %s";
            return new Formatter().format(ACCESS_GRANTED, shortenedName(), controlCode, url).toString();
        } else {
            // 115 characters failed, including rapidpass email, without bound user name
            String ACCESS_DECLINED = "Hi, %s. Your entry has been rejected due to incomplete field/s. Please register individually via RapidPass.ph to get your QR code.";
            return new Formatter().format(ACCESS_DECLINED, shortenedName(), RAPIDPASS_EMAIL).toString();
        }
    }
}
