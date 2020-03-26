package ph.devcon.rapidpass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.codehaus.plexus.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ph.devcon.rapidpass.model.QrPayload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class QrGeneratorServiceImplTest {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    QrGeneratorServiceImpl instance;

    private static String decodeQRCode(File qrCodeimage) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
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
        instance = new QrGeneratorServiceImpl(JSON_MAPPER);
    }

    @Test
    void generateQr() throws IOException, WriterException {
        final QrPayload testPayload = new QrPayload((byte) 1, 12345, new Date().getTime(), new Date().getTime(), "ABCD 1234");
        File file = instance.generateQr(
                testPayload);

        assertThat("QR code file has been creaed.", file, is(notNullValue()));

        assertThat("QR code file has been creaed.", file.exists(), is(true));

        // can visually inspect qr code image from logs

        // try decoding qr code
        final String decodedQrPayloadStr = decodeQRCode(file);
        System.out.println("decodedString = " + decodedQrPayloadStr);
        assertThat("we can decode QR Code file", decodedQrPayloadStr, is(not(emptyString())));

        // decode base 64 string
        final String decodedFromBase64 = new String(Base64.decodeBase64(decodedQrPayloadStr.getBytes()));
        System.out.println("decodedBase64 = " + decodedFromBase64);

        // expect json string that can be deserialized into QrPayload
        final QrPayload decodedQrPayload = JSON_MAPPER.readValue(decodedFromBase64, QrPayload.class);

        assertThat("decoded payload is equal to test payload", decodedQrPayload, is(testPayload));


    }
}