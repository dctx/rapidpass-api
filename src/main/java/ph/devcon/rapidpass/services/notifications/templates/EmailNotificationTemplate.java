package ph.devcon.rapidpass.services.notifications.templates;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import ph.devcon.rapidpass.enums.PassType;

import javax.validation.constraints.NotNull;
import java.util.Formatter;

@Builder
public class EmailNotificationTemplate implements NotificationTemplate<String> {

    @Value("${notifier.mailFrom:rapidpass-dctx@devcon.ph}")
    private static final String RAPIDPASS_EMAIL = "RapidPass-dctx@devcon.ph";

    @NotNull
    private PassType passType;

    /**
     * Leave this blank if the {@link ph.devcon.rapidpass.entities.AccessPass} was granted.
     * Supply this value if the {@link ph.devcon.rapidpass.entities.AccessPass} was declined.
     */
    private String reason;

    /**
     * This is a required field if the access pass was granted. It should point to the Qr Code.
     */
    private String url;

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
        return StringUtils.isEmpty(reason);
    }

    private String vehicle() {

        if (isGranted()) {
            if (StringUtils.isEmpty(url)) throw new IllegalArgumentException("The URL for the QR code must be provided");

            // 150 characters without bound variables
            String ACCESS_GRANTED = "Your entry for your vehicle has been approved. We've sent you a list of instructions on how you can use your QR code along with a printable file that you can use at the checkpoint. You can download your QR code on RapidPass.ph by following this link: %s";
            return new Formatter().format(ACCESS_GRANTED, url).toString();
        } else {
            // 129 characters failed, including rapidpass email, without bound user name
            String ACCESS_DECLINED = "Your entry for your vehicle has been rejected due to %s. Please contact %s} for further concerns and inquiry.";
            return new Formatter().format(ACCESS_DECLINED, reason, RAPIDPASS_EMAIL).toString();
        }
    }

    private String person() {

        if (isGranted()) {
            if (StringUtils.isEmpty(url)) throw new IllegalArgumentException("The URL for the QR code must be provided");

            // 150 characters without bound variables
            String ACCESS_GRANTED = "Your entry has been approved. We've sent you a list of instructions on how you can use your QR code along with a printable file that you can use at the checkpoint. You can download your QR code on RapidPass.ph by following this link: %s";
            return new Formatter().format(ACCESS_GRANTED, url).toString();
        } else {
            // 115 characters failed, including rapidpass email, without bound user name
            String ACCESS_DECLINED = "Your entry has been rejected due to %s. Please contact %s for further concerns and inquiry.";
            return new Formatter().format(ACCESS_DECLINED, reason, RAPIDPASS_EMAIL).toString();
        }
    }
}
