package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import ph.devcon.dctx.rapidpass.commons.QrCodeSerializer;
import ph.devcon.dctx.rapidpass.model.QrCodeData;

import java.io.File;
import java.io.IOException;

/**
 * Service for generating QR code images using {@link QrCodeData} objects.
 */
public interface QrGeneratorService {

    /**
     * Generates a QR from a {@link QrCodeData} object. The object is serialized by {@link QrCodeSerializer} then Base64 encoded.
     * see <a href="https://docs.google.com/document/d/13J-9MStDRL7thMm9eBgcSFU3X4b0_oeb3aikbhUZZAs/edit#">design docs</a>
     *
     * @param payload payload to transform into QR
     * @return QR code file image
     * @throws IOException     on errors in json processing or saving QR to file
     * @throws WriterException on errors in generating QR code
     * @throws IllegalArgumentException if there are missing data from the {@link QrCodeData} payload.
     */
    File generateQr(QrCodeData payload) throws IOException, WriterException, IllegalArgumentException;
}
