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

package ph.devcon.rapidpass.services;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import ph.devcon.dctx.rapidpass.commons.HmacSha256;
import ph.devcon.dctx.rapidpass.commons.QrCodeDeserializer;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QrGeneratorServiceImplTest {

    QrGeneratorServiceImpl instance;
    QrCodeDeserializer qrDeserializer;

    private String encryptionKey = "2D4B6150645367566B59703373357638792F423F4528482B4D6251655468576D";
    private String signingKey = "67566B5970337336763979244226452948404D6351665468576D5A7134743777";


    private static String decodeQRCode(byte[] qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrCodeimage));
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            System.out.println("There is no QR code in the image");
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        final byte[] encryptionKeyBytes = Hex.decode(this.encryptionKey);
        final byte[] signingKeyBytes = Hex.decode(this.signingKey);
        final QrCodeSerializer qrCodeSerializer = new QrCodeSerializer(encryptionKeyBytes);
        final Signer signer = HmacSha256.signer(signingKeyBytes);

        instance = new QrGeneratorServiceImpl(qrCodeSerializer, signer);
        qrDeserializer = new QrCodeDeserializer(encryptionKeyBytes);
    }

    private static final long CC_1234_ENCRYPTED = 2491777155L;
    private static final int MAR_23_2020 = 1584921600;
    private static final int MAR_27_2020 = 1585267200;

//    FIXME
//    @Test
    void generateQr() throws IOException, WriterException {
        final QrCodeData testPayload = QrCodeData.individual()
                .idOrPlate("ABCD 1234")
                .controlCode(CC_1234_ENCRYPTED)
                .apor("AB")
                .validFrom(MAR_23_2020)
                .validUntil(MAR_27_2020)
                .build();

        final byte[] file = instance.generateQr(testPayload);
        assertThat("QR code file has been created.", file, is(notNullValue()));

        assertThat("QR code file has been created.", file.length, is(greaterThan(0)));

        // can visually inspect qr code image from logs

        // try decoding qr code
        final String decodedQrPayloadStr = decodeQRCode(file);
        System.out.println("decodedString = " + decodedQrPayloadStr);
        assertThat("we can decode QR Code file", decodedQrPayloadStr, is(not(emptyString())));

        final byte[] qrBytes = Base64.decode(decodedQrPayloadStr);
        final QrCodeData decodedQrData = this.qrDeserializer.decode(qrBytes);
        assertThat("decoded data is same as payload", decodedQrData, is(testPayload));
    }

    @Test
    void failToGenerateQr_idOrPlateMissing() {

        assertThrows(IllegalArgumentException.class, () -> {

            // This causes the exception
            String emptyIdOrPlate = "";

            // Currently, commons package doesn't do NPE checking if the string values are non null.
            // This means, this potentially throws a NullPointerException, in the case where `Apor` or `idOrPlate` is null.
            final QrCodeData testPayload = QrCodeData.individual()
                    .idOrPlate(emptyIdOrPlate)
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor("AB")
                    .validFrom(MAR_23_2020)
                    .validUntil(MAR_27_2020)
                    .build();

            instance.generateQr(testPayload);
        });
    }

    @Test
    void failToGenerateQr_aporCodeMissing() {

        assertThrows(IllegalArgumentException.class, () -> {

            // This causes the exception
            String emptyIdOrPlate = "";

            // Currently, commons package doesn't do NPE checking if the string values are non null.
            // This means, this potentially throws a NullPointerException, in the case where `Apor` or `idOrPlate` is null.
            final QrCodeData testPayload = QrCodeData.individual()
                    // This causes the exception
                    .idOrPlate("ABC 123")
                    .controlCode(CC_1234_ENCRYPTED)
                    .apor("")
                    .validFrom(MAR_23_2020)
                    .validUntil(MAR_27_2020)
                    .build();

            instance.generateQr(testPayload);
        });
    }
}
