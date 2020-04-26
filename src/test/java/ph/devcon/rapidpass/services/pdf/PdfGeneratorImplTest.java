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

package ph.devcon.rapidpass.services.pdf;

import com.google.zxing.WriterException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.services.QrGeneratorServiceImpl;
import ph.devcon.rapidpass.utilities.DateFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;

class PdfGeneratorImplTest {

    QrGeneratorServiceImpl qrGeneratorService;

    private static final String TEST_PDF_OUTPUT_FILENAME = "test.pdf";

    private String encryptionKey = "2D4B6150645367566B59703373357638792F423F4528482B4D6251655468576D";
    private String signingKey = "67566B5970337336763979244226452948404D6351665468576D5A7134743777";

    @BeforeEach
    void setUp() {
        final byte[] encryptionKeyBytes = Hex.decode(this.encryptionKey);
        final byte[] signingKeyBytes = Hex.decode(this.signingKey);
        final QrCodeSerializer qrCodeSerializer = new QrCodeSerializer(encryptionKeyBytes);
        final Signer signer = HmacSha256.signer(signingKeyBytes);

        qrGeneratorService = new QrGeneratorServiceImpl(qrCodeSerializer, signer);
    }

    private static final long CC_1234_ENCRYPTED = 2491777155L;
    private static final int MAR_23_2020 = 1584921600;
    private static final int MAR_27_2020 = 1585267200;

    @Test
    @Ignore
    void generatePdf() throws Exception {

        // Mock data
        Date MAR_23_2020_UTC = new Date((long) MAR_23_2020 * 1000);
        Date MAR_27_2020_UTC = new Date((long) MAR_27_2020 * 1000);

        String formattedStart = DateFormatter.machineFormat(MAR_23_2020_UTC);
        String formattedEnd = DateFormatter.machineFormat(MAR_27_2020_UTC);

        RapidPass mockRapidPassData = RapidPass.builder()
                .passType(INDIVIDUAL)
                .name("Jonas Jose Almendras Domingo Delas Alas")
                .controlCode("12345")
                .idType("Driver's License")
                .identifierNumber("N01-234235345")
                .aporType("NR")
                .company("Banco ng Pilipinas Incorporated International")
                .controlCode("3J12K5AV")
                .validFrom(formattedStart)
                .validUntil(formattedEnd)
                .build();

        // Create QR cod payload
        QrCodeData testPayload = null;

        if (INDIVIDUAL.equals(mockRapidPassData.getPassType())) {
            testPayload = QrCodeData.individual()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .validUntil((int)(MAR_27_2020_UTC.getTime() / 1000))
                    .build();
        } else if (VEHICLE.equals(mockRapidPassData.getPassType())) {
            testPayload = QrCodeData.vehicle()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .validUntil((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .build();
        }

        generatePdf(mockRapidPassData, testPayload);
    }


    @Test
    @Ignore
    void generatePdfWithLongName() throws Exception {


        // Mock data
        Date MAR_23_2020_UTC = new Date((long) MAR_23_2020 * 1000);
        Date MAR_27_2020_UTC = new Date((long) MAR_27_2020 * 1000);

        String formattedStart = DateFormatter.machineFormat(MAR_23_2020_UTC);
        String formattedEnd = DateFormatter.machineFormat(MAR_27_2020_UTC);

        RapidPass mockRapidPassData = RapidPass.builder()
                .passType(INDIVIDUAL)
                .name("José Protasio Rizal Mercado y Alonso Realonda")
                .idType("Driver's License")
                .identifierNumber("N01-234235345")
                .aporType("NR")
                .originCity("Marikina City")
                .destCity("Paranaque City")
                .company("Kataas-taasang, Kagalang-galangang Katipunan")
                .controlCode("3J12K5AV")
                .validFrom(formattedStart)
                .validUntil(formattedEnd)
                .build();

        // Create QR cod payload
        QrCodeData testPayload = null;

        if (INDIVIDUAL.equals(mockRapidPassData.getPassType())) {
            testPayload = QrCodeData.individual()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .validUntil((int)(MAR_27_2020_UTC.getTime() / 1000))
                    .build();
        } else if (VEHICLE.equals(mockRapidPassData.getPassType())) {
            testPayload = QrCodeData.vehicle()
                    .idOrPlate(mockRapidPassData.getIdentifierNumber())
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor(mockRapidPassData.getAporType())
                    .validFrom((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .validUntil((int)(MAR_23_2020_UTC.getTime() / 1000))
                    .build();
        }

        generatePdf(mockRapidPassData, testPayload);
    }

    void generatePdf(RapidPass rapidPass, QrCodeData testPayload) throws IOException, WriterException, ParseException {

        assertThat(testPayload, not(equalTo(null)));

        // todo, will want to scale processing to streams instead of persisting to filespace once we get many users! in memory is always faster!

        // generate QR Code to embed in PDF
        final byte[] qrCodeBytes = qrGeneratorService.generateQr(testPayload);

        // do pdf generation
        PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl();
        final ByteArrayOutputStream outputStream = (ByteArrayOutputStream) pdfGenerator.generatePdf(qrCodeBytes, rapidPass);

        FileOutputStream fileOutputStream = new FileOutputStream("test.pdf");

        byte[] bytes = outputStream.toByteArray();
        fileOutputStream.write(bytes);

        fileOutputStream.flush();
        fileOutputStream.close();

        File file = new File(TEST_PDF_OUTPUT_FILENAME);
        file.delete();

        assertThat("pdf is being streamed", outputStream.toByteArray().length, is(greaterThan(0)));
    }
}
