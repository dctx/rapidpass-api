package ph.devcon.rapidpass.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.commons.Signer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
     * QR code width in pixels. Default to 500px. Configurable via {@code rapidpass.qr.width} property.
     */
    @Value("${rapidpass.qr.width:500}")
    private int qrWidth = 800;
    /**
     * QR code height in pixels. Default to 500px. Configurable via {@code rapidpass.qr.height} property.
     */
    @Value("${rapidpass.qr.height:500}")
    private int qrHeight = 800;

    @Autowired
    private final QrCodeSerializer qrCodeSerializer;

    @Autowired
    private final Signer signer;

    @Override
    public byte[] generateQr(QrCodeData payload) throws IOException, WriterException {
        if (payload.getAporCode() == null || "".equals(payload.getAporCode())) {
            throw new IllegalArgumentException("The QrCodeData.aporCode is needed to generate the QR.");
        }

        if (payload.getIdOrPlate() == null || "".equals(payload.getIdOrPlate())) {
            throw new IllegalArgumentException("The QrCodeData.getIdOrPlate is needed to generate the QR.");
        }

        if (payload.getValidFrom() == 0) {
            throw new IllegalArgumentException("The QrCodeData.validFrom date is needed to generate the QR.");
        }

        if (payload.getValidUntil() == 0) {
            throw new IllegalArgumentException("The QrCodeData.validUntil is needed to generate the QR.");
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix matrix = qrCodeWriter.encode(
                serializePayload(payload),
                BarcodeFormat.QR_CODE, qrWidth, qrHeight);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", output);
        log.debug("generated qr for {}", payload);
        return output.toByteArray();
    }

    /**
     * Serializes a {@link QrCodeData} object using {@link QrCodeSerializer} then encodes to Base64.
     *
     * @param payload payload to serialize and sign.
     * @return Base64 string
     */
    String serializePayload(QrCodeData payload) {
        final byte[] qrBytes = this.qrCodeSerializer.serializeAndSign(payload, this.signer);
        return Base64.toBase64String(qrBytes);
    }


}
