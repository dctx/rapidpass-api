package ph.devcon.rapidpass.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.models.RapidPassRequest;
import ph.devcon.rapidpass.services.QrGeneratorServiceImpl;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ph.devcon.rapidpass.models.RapidPassRequest.AccessType.MED;
import static ph.devcon.rapidpass.models.RapidPassRequest.PassType.INDIVIDUAL;

class PdfGeneratorTest {

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
        // create QR cod payload
        final QrCodeData testPayload = QrCodeData.individual()
                .idOrPlate("ABCD 1234")
                .controlCode(CC_1234_ENCRYPTED)
                .purpose('D')
                .validFrom(MAR_23_2020)
                .validUntil(MAR_27_2020)
                .build();

        // todo, will want to scale processing to streams instead of persisting to filespace once we get many users! in memory is always faster!

        // generate QR Code to embed in PDF
        final File qrCodeFile = qrGeneratorService.generateQr(testPayload);
        final String pdfPath = "PdfGeneratorTest-test.pdf";
        // delete test file to make sure its not there at the moment
        final File tmpFile = new File(pdfPath);
        tmpFile.delete();
        assertThat("pdf file is created!", tmpFile, is(not(FileMatchers.anExistingFile())));

        // do pdf generation
        final File pdfFile =
                PdfGenerator.generatePdf(pdfPath,
                        qrCodeFile,
                        RapidPassRequest.builder()
                                .passType(INDIVIDUAL)
                                .firstName("Jonas Was Here")
                                .lastName("Donasco")
                                .accessType(MED)
                                .company("DEVCON")
                                .build());

        assertThat("pdf file is created!", pdfFile, is(FileMatchers.anExistingFile()));
        assertThat("pdf file is created!", pdfFile, is(FileMatchers.aFileWithSize(greaterThan(0L))));

        // cleanup!
        pdfFile.delete();
    }
}