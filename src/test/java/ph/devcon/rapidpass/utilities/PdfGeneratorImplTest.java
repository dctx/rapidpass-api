package ph.devcon.rapidpass.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.io.FileMatchers;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.QrGeneratorServiceImpl;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorImpl;

import java.io.File;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

class PdfGeneratorImplTest {

    QrGeneratorServiceImpl qrGeneratorService;


    @BeforeEach
    void setUp() {
        qrGeneratorService = new QrGeneratorServiceImpl(new ObjectMapper());
    }

    private static final long CC_1234_ENCRYPTED = 2491777155L;
    private static final int MAR_23_2020 = 1584921600;
    private static final int MAR_27_2020 = 1585267200;

    @Test
    void generatePdf() throws Exception {

        // Mock data
        Date MAR_23_2020_UTC = new Date((long) MAR_23_2020 * 1000);
        Date MAR_27_2020_UTC = new Date((long) MAR_27_2020 * 1000);

        RapidPass mockRapidPassData = RapidPass.builder()
                .passType(INDIVIDUAL.toString())
                .name("Jonas Jose Almendras Domingo")
                .controlCode("12345")
                .idType("Driver's License")
                .identifierNumber("N01-234235345")
                .aporType("NR")
                .company("Banco ng Pilipinas Incorporated")
                .controlCode("#NCR9NP")
                .validFrom(MAR_23_2020_UTC)
                .validTo(MAR_27_2020_UTC)
                .build();

        // Create QR cod payload
        QrCodeData testPayload = null;

        if (mockRapidPassData.getPassType().equals(INDIVIDUAL.toString())) {
            testPayload = QrCodeData.individual()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(mockRapidPassData.getValidFrom().getTime() / 1000))
                    .validUntil((int)(mockRapidPassData.getValidTo().getTime() / 1000))
                    .build();
        } else if (mockRapidPassData.getPassType().equals(VEHICLE.toString())) {
            testPayload = QrCodeData.vehicle()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(mockRapidPassData.getValidFrom().getTime() / 1000))
                    .validUntil((int)(mockRapidPassData.getValidTo().getTime() / 1000))
                    .build();
        }

        assertThat(testPayload, not(equalTo(null)));

        // todo, will want to scale processing to streams instead of persisting to filespace once we get many users! in memory is always faster!

        // generate QR Code to embed in PDF
        final File qrCodeFile = qrGeneratorService.generateQr(testPayload);
        final String pdfPath = "PdfGeneratorTest-test.pdf";
        // delete test file to make sure its not there at the moment
        final File tmpFile = new File(pdfPath);
        tmpFile.delete();
        assertThat("pdf file is created!", tmpFile, is(not(FileMatchers.anExistingFile())));

        // do pdf generation
        PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl();
        final File pdfFile = pdfGenerator.generatePdf(pdfPath, qrCodeFile, mockRapidPassData);

        assertThat("pdf file is created!", pdfFile, is(FileMatchers.anExistingFile()));
        assertThat("pdf file is created!", pdfFile, is(FileMatchers.aFileWithSize(greaterThan(0L))));

        // cleanup!
//        pdfFile.delete();
    }

    @Test
    @Ignore
    void generatePdfWithLongName() throws Exception {

        // Mock data
        Date MAR_23_2020_UTC = new Date((long) MAR_23_2020 * 1000);
        Date MAR_27_2020_UTC = new Date((long) MAR_27_2020 * 1000);

        RapidPass mockRapidPassData = RapidPass.builder()
                .passType(INDIVIDUAL.toString())
                .name("Jonas Jose Almendras Domingo Whose Name is Very Long Very Long")
                .controlCode("12345")
                .idType("Driver's License")
                .identifierNumber("N01-234235345")
                .aporType("NR")
                .company("Banco ng Pilipinas Incorporated Which Could Be Very Long Very")
                .controlCode("#NCR9NP")
                .validFrom(MAR_23_2020_UTC)
                .validTo(MAR_27_2020_UTC)
                .build();

        // Create QR cod payload
        QrCodeData testPayload = null;

        if (mockRapidPassData.getPassType().equals(INDIVIDUAL.toString())) {
            testPayload = QrCodeData.individual()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(mockRapidPassData.getValidFrom().getTime() / 1000))
                    .validUntil((int)(mockRapidPassData.getValidTo().getTime() / 1000))
                    .build();
        } else if (mockRapidPassData.getPassType().equals(VEHICLE.toString())) {
            testPayload = QrCodeData.vehicle()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(mockRapidPassData.getValidFrom().getTime() / 1000))
                    .validUntil((int)(mockRapidPassData.getValidTo().getTime() / 1000))
                    .build();
        }

        assertThat(testPayload, not(equalTo(null)));

        // todo, will want to scale processing to streams instead of persisting to filespace once we get many users! in memory is always faster!

        // generate QR Code to embed in PDF
        final File qrCodeFile = qrGeneratorService.generateQr(testPayload);
        final String pdfPath = "PdfGeneratorTest-test.pdf";
        // delete test file to make sure its not there at the moment
        final File tmpFile = new File(pdfPath);
        tmpFile.delete();
        assertThat("pdf file is created!", tmpFile, is(not(FileMatchers.anExistingFile())));

        // do pdf generation
        PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl();
        final File pdfFile = pdfGenerator.generatePdf(pdfPath, qrCodeFile, mockRapidPassData);

        assertThat("pdf file is created!", pdfFile, is(FileMatchers.anExistingFile()));
        assertThat("pdf file is created!", pdfFile, is(FileMatchers.aFileWithSize(greaterThan(0L))));

        // cleanup!
//        pdfFile.delete();
    }
}