package ph.devcon.rapidpass.services.pdf;

import ph.devcon.rapidpass.models.RapidPass;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

public interface PdfGeneratorService {
    OutputStream generatePdf(byte[] qrCodeFile, RapidPass rapidPass) throws ParseException, IOException;
}
