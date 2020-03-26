package com.devcon.rapidpass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import com.itextpdf.kernel.pdf.PdfDocument;

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
        approvedRapidPass.setPassType("xxxxxx");
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

    //Not an actual test
    @Test
    public void individualPdfGeneration() {
        ApprovedRapidPass approvedRapidPass = createObject();
        approvedRapidPass.setPassType("individual");

        PdfGenerator pdfGenerator = new PdfGenerator("src/main/resources/generated-pdf.pdf");

        PdfDocument result =  pdfGenerator.generatePdf(new File("src/main/resources/CxEncSerializedPass.png"), approvedRapidPass);

    }

    @Test
    public void vehiclePdfGeneration() {
        ApprovedRapidPass approvedRapidPass = createObject();
        approvedRapidPass.setPassType("VEHICLE");

        PdfGenerator pdfGenerator = new PdfGenerator("src/main/resources/generated-pdf.pdf");

        PdfDocument result =  pdfGenerator.generatePdf(new File("src/main/resources/CxEncSerializedPass.png"), approvedRapidPass);

    }
}