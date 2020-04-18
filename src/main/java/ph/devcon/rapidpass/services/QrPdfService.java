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

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ph.devcon.dctx.rapidpass.model.QrCodeData;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.AccessPassStatus;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.repositories.AccessPassRepository;
import ph.devcon.rapidpass.services.controlcode.ControlCodeService;
import ph.devcon.rapidpass.services.pdf.PdfGeneratorImpl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
     * Handler for everything related to control code.
     */
    private final ControlCodeService controlCodeService;

    /**
     * Generates a PDF containing the QR code pertaining to the passed in reference ID. The PDF file is already
     * converted to bytes for easy sending to HTTP.
     *
     * @param controlCode controlCode of access pass
     * @return bytes of PDF file containing the QR code
     * @throws IOException     see {@link QrGeneratorService#generateQr(QrCodeData)}
     * @throws WriterException see {@link QrGeneratorService#generateQr(QrCodeData)}
     */
    public OutputStream generateQrPdf(String controlCode) throws ParseException, IOException, WriterException {

        AccessPass accessPass = controlCodeService.findAccessPassByControlCode(controlCode);

        if (accessPass == null)
            throw new IllegalArgumentException("Failed to find AccessPass with controlCode=" + controlCode);

        accessPass = controlCodeService.bindControlCodeForAccessPass(accessPass);

        byte[] qrImage = generateQrImageData(accessPass);

        // generate qr pdf
        PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl();

        String temporaryFile = File.createTempFile("qrPdf", ".pdf").getAbsolutePath();

        return pdfGenerator.generatePdf(qrImage, RapidPass.buildFrom(accessPass));
    }

    /**
     * @param accessPass The access pass whose QR will be generated.
     * @return a file which points to the image data
     * @throws IOException     see {@link QrGeneratorService#generateQr(QrCodeData)}
     * @throws WriterException see {@link QrGeneratorService#generateQr(QrCodeData)}
     */
    public byte[] generateQrImageData(AccessPass accessPass) throws IOException, WriterException {
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

        if (accessPass.getId() == null) {
            throw new IllegalArgumentException("AccessPass.id is a required parameter for rendering the PDF.");
        }

        String controlCode = controlCodeService.encode(accessPass.getId());

        // generate qr code data from access pass
        final QrCodeData qrCodeData = AccessPass.toQrCodeData(accessPass, controlCode);

        // generate qr image file
        return qrGeneratorService.generateQr(qrCodeData);
    }
}
