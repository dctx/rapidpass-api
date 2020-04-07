package ph.devcon.rapidpass.entities;

import org.junit.jupiter.api.Test;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassCSVdata;
import ph.devcon.rapidpass.models.RapidPassRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class RapidPassRequestTest {

    @Test
    void testTrimWhitespaces() {

        RapidPassCSVdata data = new RapidPassCSVdata();
        data.setPassType("INDIVIDUAL");
        data.setAporType(" AB     ");

        data.setFirstName(" Jose");
        data.setMiddleName(" Alonzo Mercado");
        data.setLastName(" Rizal  ");

        data.setRemarks(" a very long string with whitespaces around it     ");

        data.setOriginCity("   Paranaque   ");
        data.setOriginName("  Jollibee ");
        data.setOriginStreet("   Dona Soledad ");
        data.setOriginProvince(" Metro Manila ");

        data.setDestCity("   Makati   ");
        data.setDestName("   McDonalds   ");
        data.setDestStreet("  Ayala Ave. ");
        data.setDestProvince(" Metro Manila   ");

        data.setMobileNumber("  09171234567  ");
        data.setPlateNumber("abC-234");

        RapidPassRequest rapidPassRequest = RapidPassRequest.buildFrom(data);

        assertThat(rapidPassRequest.getPassType(),  equalTo(PassType.INDIVIDUAL));
        assertThat(rapidPassRequest.getAporType(), equalTo("AB"));

        assertThat(rapidPassRequest.getFirstName(), equalTo("Jose"));
        assertThat(rapidPassRequest.getMiddleName(), equalTo("Alonzo Mercado"));
        assertThat(rapidPassRequest.getLastName(), equalTo("Rizal"));

        assertThat(rapidPassRequest.getRemarks(), equalTo("a very long string with whitespaces around it"));

        assertThat(rapidPassRequest.getOriginCity(), equalTo("Paranaque"));
        assertThat(rapidPassRequest.getOriginName(), equalTo("Jollibee"));
        assertThat(rapidPassRequest.getOriginStreet(), equalTo("Dona Soledad"));
        assertThat(rapidPassRequest.getOriginProvince(), equalTo("Metro Manila"));

        assertThat(rapidPassRequest.getDestCity(), equalTo("Makati"));
        assertThat(rapidPassRequest.getDestName(), equalTo("McDonalds"));
        assertThat(rapidPassRequest.getDestStreet(), equalTo("Ayala Ave."));
        assertThat(rapidPassRequest.getDestProvince(), equalTo("Metro Manila"));

        assertThat(rapidPassRequest.getMobileNumber(), equalTo("09171234567"));
        assertThat(rapidPassRequest.getPlateNumber(),  equalTo("ABC234"));



    }

    @Test
    void doesNotThrowErrorsForEmptyRapidPassRequestRow() {

        RapidPassCSVdata data = new RapidPassCSVdata();

        try {
            RapidPassRequest rapidPassRequest = RapidPassRequest.buildFrom(data);
        } catch (Exception e) {
            fail("Should not throw an exception", e);
        }

    }
}
