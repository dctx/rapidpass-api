package ph.devcon.rapidpass.services;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;

/**
 * This service combines PDF generator and QR generator to create PDF's of generated QR codes.
 */
@Service
@RequiredArgsConstructor
public class QrPdfService {

    /**
     * Service for generating QR code files.
     */
    private final QrGeneratorService qrGeneratorService;
    /**
     * Repo for querying access paths.
     */
    private final AccessPassRepository accessPassRepository;

    /**
     * Generates a PDF containing the QR code pertaining to the passed in reference ID. The PDF file is already converted to bytes for easy sending to HTTP.
     *
     * @param referenceId reference id of access pass
     * @return bytes of PDF file containing the QR code
     * @throws IOException     on error writing the PDF
     * @throws WriterException on error writing the QR code
     */
    public byte[] generateQrPdf(String referenceId) throws ParseException, IOException, WriterException {
        return generateQrPdf(accessPassRepository.findByReferenceID(referenceId));
    }

    /**
     * Generates a PDF containing the QR code pertaining to the passed in Access Pass. The PDF file is already converted to bytes for easy sending to HTTP.
     *
     * @param accessPass Access Pass
     * @return bytes of PDF file containing the QR code
     * @throws IOException     on error writing the PDF
     * @throws WriterException on error writing the QR code
     */
    public byte[] generateQrPdf(AccessPass accessPass) throws IOException, WriterException, ParseException {


        if ("".equals(accessPass.getName()) || accessPass.getName() == null) {
            throw new IllegalArgumentException("AccessPass.name is a required parameter for rendering the PDF.");
        }

        if (!AccessPassStatus.APPROVED.toString().equalsIgnoreCase(accessPass.getStatus())) {
            throw new IllegalArgumentException("Cannot render PDF with QR for an AccessPass that is not yet approved.");
        }

        if ("".equals(accessPass.getCompany()) || accessPass.getCompany() == null) {
            throw new IllegalArgumentException("AccessPass.company is a required parameter for rendering the PDF.");
        }

        if ("".equals(accessPass.getAporType()) || accessPass.getAporType() == null) {
            throw new IllegalArgumentException("AccessPass.aporType is a required parameter for rendering the PDF.");
        }

        if ("".equals(accessPass.getPassType()) || accessPass.getPassType() == null) {
            throw new IllegalArgumentException("AccessPass.passType is a required parameter for rendering the PDF.");
        }

        if (accessPass.getValidFrom() == null) {
            throw new IllegalArgumentException("AccessPass.validFrom is a required parameter for rendering the PDF.");
        }

        if (accessPass.getValidTo() == null) {
            throw new IllegalArgumentException("AccessPass.validTo is a required parameter for rendering the PDF.");
        }

        // generate qr code data from access pass
        final QrCodeData qrCodeData = AccessPass.toQrCodeData(accessPass);

        // generate qr image file
        final File qrImage = qrGeneratorService.generateQr(qrCodeData);

        // generate qr pdf
        PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl();

        String temporaryFile = File.createTempFile("qrPdf", ".pdf").getAbsolutePath();

        final File qrPdf = pdfGenerator.generatePdf(temporaryFile, qrImage, RapidPass.buildFrom(accessPass));

        // send over as bytes
        return Files.readAllBytes(qrPdf.toPath());
    }
}