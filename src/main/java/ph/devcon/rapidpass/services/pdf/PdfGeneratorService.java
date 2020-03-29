package ph.devcon.rapidpass.services.pdf;

import ph.devcon.rapidpass.models.RapidPass;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public interface PdfGeneratorService {
    File generatePdf(String filePath,File qrCodeFile, RapidPass rapidPass) throws FileNotFoundException, MalformedURLException;
}
