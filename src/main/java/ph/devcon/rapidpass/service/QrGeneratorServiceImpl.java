package ph.devcon.rapidpass.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * The {@link QrGeneratorServiceImpl} implements{@link QrGeneratorService} using the excellent Zebra Crossing barcode library.
 * See <a href="https://github.com/zxing/zxing">zxing</a> documentation
 *
 * @author jonasespelita@gmail.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QrGeneratorServiceImpl implements QrGeneratorService {

    /**
     * Spring Jackson JSON serializer.
     */
    private final ObjectMapper jsonMapper;

    /**
     * QR code width in pixels. Default to 500px. Configurable via {@code rapidpass.qr.width} property.
     */
    @Value("${rapidpass.qr.width:500}")
    private int qrWidth = 500;
    /**
     * QR code height in pixels. Default to 500px. Configurable via {@code rapidpass.qr.height} property.
     */
    @Value("${rapidpass.qr.height:500}")
    private int qrHeight = 500;


    @Override
    public File generateQr(QrCodeData payload) throws IOException, WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(
                serializePayload(payload),
                BarcodeFormat.QR_CODE, qrWidth, qrHeight);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        File qr = File.createTempFile("qr-", ".png");
        ImageIO.write(image, "PNG", qr);
        // todo write to streams instead of files for better performance, no write to disk na!

        // TODO: overlay rapid pass logo?

        log.debug("Saved QR image to {}", qr.getAbsolutePath());
        return qr;
    }

    /**
     * Serializes a {@link QrCodeData} object using {@link QrCodeSerializer} then encodes to Base64.
     *
     * @param payload payload to serialize
     * @return Base64 string
     */
    String serializePayload(QrCodeData payload) {
        return Base64.getEncoder().encodeToString(QrCodeSerializer.encode(payload));
    }
}
