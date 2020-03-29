package ph.devcon.rapidpass.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author jonasespelita@gmail.com
 */
class AccessPassTest {
    @Test
    void isValid() {
        final OffsetDateTime now = OffsetDateTime.now();

        final AccessPass validAccessPass = AccessPass.builder().
                status("APPROVED")
                .passType("INDIVIDUAL")
                .controlCode("123456")
                .idType("Driver's License")
                .identifierNumber("N0124734213")
                .name("Darren Karl A. Sapalo")
                .company("DevCon.ph")
                .aporType("AB")
                .validFrom(now)
                .validTo(now.plusDays(1))
                .build();

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
                .validFrom(now)
                .validTo(now.plusDays(1))
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
                .validFrom(now.minusDays(10))
                .validTo(now.minusDays(1))
                .build();
        assertFalse(AccessPass.isValid(pendingAccessPass), "expired pass should return false");


    }
}