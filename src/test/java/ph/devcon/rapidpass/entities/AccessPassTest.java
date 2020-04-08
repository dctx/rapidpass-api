package ph.devcon.rapidpass.entities;

import org.junit.jupiter.api.Test;
import ph.devcon.dctx.rapidpass.model.ControlCode;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author jonasespelita@gmail.com
 */
class AccessPassTest {

    final static OffsetDateTime NOW = OffsetDateTime.now();

    final static AccessPass INDIVIDUAL_ACCESS_PASS = AccessPass.builder().
            status("APPROVED")
            .passType("INDIVIDUAL")
            .controlCode(ControlCode.encode(38))
            .idType("Driver's License")
            .identifierNumber("N0124734213")
            .name("Darren Karl A. Sapalo")
            .company("DevCon.ph")
            .aporType("AB")
            .validFrom(NOW)
            .validTo(NOW.plusDays(1))
            .build();
    final static AccessPass VEHICLE_ACCESS_PASS = AccessPass.builder().
            status("APPROVED")
            .passType("VEHICLE")
            .controlCode(ControlCode.encode(38))
            .idType("Driver's License")
            .identifierNumber("ABCD 1234")
            .aporType("AB")
            .validFrom(NOW)
            .validTo(NOW.plusDays(1))
            .build();

    @Test
    void isValid() {

        final AccessPass validAccessPass = INDIVIDUAL_ACCESS_PASS;

        assertTrue(AccessPass.isValid(validAccessPass), "valid pass should return true");

        // invalid coz PENDING
        final AccessPass pendingAccessPass = AccessPass.builder().
                status("PENDING")
                .passType("INDIVIDUAL")
                .controlCode("123456")
                .idType("Driver's License")
                .identifierNumber("N0124734213")
                .name("Darren Karl A. Sapalo")
                .company("DevCon.ph")
                .aporType("AB")
                .validFrom(NOW)
                .validTo(NOW.plusDays(1))
                .build();

        assertFalse(AccessPass.isValid(pendingAccessPass), "pending pass should return false");

        // invalid coz EXPIRED
        final AccessPass expiredAccessPass = AccessPass.builder().
                status("APPROVED")
                .passType("INDIVIDUAL")
                .controlCode("123456")
                .idType("Driver's License")
                .identifierNumber("N0124734213")
                .name("Darren Karl A. Sapalo")
                .company("DevCon.ph")
                .aporType("AB")
                .validFrom(NOW.minusDays(10))
                .validTo(NOW.minusDays(1))
                .build();
        assertFalse(AccessPass.isValid(expiredAccessPass), "expired pass should return false");
    }

    @Test
    void toQrCodeData_INDIVIDUAL() {
        String controlCode = ControlCode.encode(1);

        final QrCodeData qrCodeData = AccessPass.toQrCodeData(INDIVIDUAL_ACCESS_PASS, controlCode);

        long decodedControlCode = ControlCode.decode(controlCode);

        assertThat(qrCodeData.getAporCode(), is(INDIVIDUAL_ACCESS_PASS.getAporType()));
        assertThat(qrCodeData.getControlCode(), is(decodedControlCode));
        assertThat(qrCodeData.isVehiclePass(), is(false));
    }

    @Test
    void toQrCodeData_VEHICLE() {
        String controlCode = VEHICLE_ACCESS_PASS.getControlCode();

        final QrCodeData qrCodeData = AccessPass.toQrCodeData(VEHICLE_ACCESS_PASS, controlCode);

        long decodedControlCode = ControlCode.decode(VEHICLE_ACCESS_PASS.getControlCode());

        assertThat(qrCodeData.getAporCode(), is(VEHICLE_ACCESS_PASS.getAporType()));
        assertThat(qrCodeData.getControlCode(), is(decodedControlCode));
        assertThat(qrCodeData.isVehiclePass(), is(true));
    }
}