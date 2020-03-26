package ph.devcon.rapidpass.service;

import com.google.zxing.WriterException;
import ph.devcon.rapidpass.model.QrPayload;

import java.io.File;
import java.io.IOException;

/**
 * Service for generating QR code images using {@link QrPayload} objects.
 */
public interface QrGeneratorService {

    /**
     * Generates a QR from a {@link QrPayload} object. The object is serialized into Avro then Base64 encoded.
     * see <a href="https://docs.google.com/document/d/13J-9MStDRL7thMm9eBgcSFU3X4b0_oeb3aikbhUZZAs/edit#">design docs</a>
     *
     * @param payload payload to transform into QR
     * @return QR code file image
     * @throws IOException     on errors in json processing or saving QR to file
     * @throws WriterException on errors in generating QR code
     */
    File generateQr(QrPayload payload) throws IOException, WriterException;
}
