package com.devcon.rapidpass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import ph.devcon.rapidpass.api.models.ApprovedRapidPass;
import ph.devcon.rapidpass.utilities.PdfGenerator;

public class PdfGeneratorTest {

    // generates the ApprovedRapidPass object
    private ApprovedRapidPass createObject() {
        ApprovedRapidPass approvedRapidPass = new ApprovedRapidPass();
        approvedRapidPass.setName("Juan Dela Kruz");
        approvedRapidPass.setControlNumber("CTRL-###");
        approvedRapidPass.setAccessType("Access Type");
        approvedRapidPass.setPassType("Pass Type");
        approvedRapidPass.setCompany("Company");
        approvedRapidPass.setPlateNum("Plate Number");
        approvedRapidPass.setMobileNumber("09123456789");
        approvedRapidPass.setEmail("xxxxx@email.mail");
        approvedRapidPass.setDestAddress("address");
        approvedRapidPass.setIdType("philhealth");
        approvedRapidPass.setIdNumber("xxxxxxxxx");
        approvedRapidPass.setRemarks("Remarks");

        return approvedRapidPass;
    }

    @Test
    public void individualPdfGenerationTest() {
        ApprovedRapidPass approvedRapidPass = createObject();

        PdfGenerator pdfGenerator = new PdfGenerator("src/main/resources/generated-pdf.pdf");
        int result =  pdfGenerator.generateIndividualPdf("src/main/resources/CxEncSerializedPass.png", approvedRapidPass);

        assertEquals(result, 1);
    }

    @Test
    public void vehiclePdfGeneratorTest() {
        ApprovedRapidPass approvedRapidPass = createObject();

        PdfGenerator pdfGenerator = new PdfGenerator("src/main/resources/generated-pdf.pdf");
        int result =  pdfGenerator.generateVehiclePdf("src/main/resources/CxEncSerializedPass.png", approvedRapidPass);

        assertEquals(result, 1);
    }

    //Failing Test: no qrcode image path
    @Test
    public void pdfGeneratorFailingTest() {
        ApprovedRapidPass approvedRapidPass = createObject();

        PdfGenerator pdfGenerator = new PdfGenerator("src/main/resources/generated-pdf.pdf");
        int result =  pdfGenerator.generateIndividualPdf("  ", approvedRapidPass);

        // blank qrcode path given. result should be 0
        assertNotEquals(result, 1);
    }
}