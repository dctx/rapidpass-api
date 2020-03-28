package ph.devcon.rapidpass.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import ph.devcon.rapidpass.model.RapidPassRequest;
import ph.devcon.rapidpass.model.RapidPassRequest.PassType;

import static ph.devcon.rapidpass.model.RapidPassRequest.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.model.RapidPassRequest.PassType.VEHICLE;


/**
 * Utility class for generating Pdf using File qrcode file from QRCode Generator.
 */
@Slf4j
public class PdfGenerator {


    /**
     * Path to the logo that will be appended to the PDF.
     */
    private static final String DCTX_LOGO_PATH = "light-bg.png";


    private PdfGenerator() {
        // noop
    }

    /**
     * Creates the pdf document and set its properties.
     *
     * @param filepath path where pdf is saved
     * @return PDF document
     * @throws FileNotFoundException error creating file.
     */
    private static Document createDocument(String filepath) throws FileNotFoundException {

        PdfDocument pdfdocument = new PdfDocument(new PdfWriter(filepath));

        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
        document.setMargins(-50, -50, -50, -50);

        return document;
    }

    /**
     * Prepare image.
     *
     * @param imagePath path to image in resource folder
     * @return image data
     * @throws MalformedURLException if path not an image
     */
    private static ImageData prepareImage(String imagePath) throws MalformedURLException {
        log.debug("preparingImage {}", imagePath);

        // get image from resource classpath
        return ImageDataFactory.create(imagePath);
    }

    /**
     * Generates a pdf at designated file path
     *
     * @param filePath          path of file where to save pdf
     * @param qrCodeFile        Image file for QR code
     * @param approvedRapidPass approved rapid pass model
     * @return file object of generated pdf
     */
    public static File generatePdf(String filePath,
                                   File qrCodeFile,
                                   RapidPassRequest approvedRapidPass)
            throws FileNotFoundException, MalformedURLException {
        log.debug("generating pdf at {}", filePath);

        Document document = createDocument(filePath);

        //qrcode image
        Image qrcode = new Image(prepareImage(qrCodeFile.getAbsolutePath()));

        // get dctx logo from classpath resource
        Image dctxLogo = new Image(prepareImage(
                ResourceUtils.getFile("classpath:light-bg.png")
                        .getAbsolutePath()));

        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400, 0);

        //processes the data that will be on the pdf

        //properties for control number
        Paragraph controlNumbeParagraph = new Paragraph();
        controlNumbeParagraph.setFontSize(44);
        controlNumbeParagraph.setTextAlignment(TextAlignment.CENTER);
        controlNumbeParagraph.setMarginTop(-50);
        controlNumbeParagraph.setBold();
        controlNumbeParagraph.add(approvedRapidPass.getControlCode() + "\n");

        Paragraph details = new Paragraph();

        // checks if pass type is individual or vehicle
        final PassType passType = approvedRapidPass.getPassType();
        if (passType.equals(INDIVIDUAL)) {
            details.setFontSize(20);
            details.setMarginLeft(70);
            details.add("Name:\t" + approvedRapidPass.getName() + "\n" +
                    "APOR Type:\t" + approvedRapidPass.getAporType() + "\n" +
                    "Pass Type:\t" + passType + "\n" +
                    "Company:\t" + approvedRapidPass.getCompany());
        } else if (passType.equals(VEHICLE)) {
            details.setFontSize(20);
            details.setMarginLeft(70);
            details.add("AOR Type:\t" + approvedRapidPass.getAporType() + "\n" +
                    "Pass Type:\t" + passType + "\n" +
                    "Plate:\t" + approvedRapidPass.getPlateOrId() + "\n" +
                    "Name:\t" + approvedRapidPass.getName());
        }

        //writes to the document
        document.add(qrcode);
        document.add(controlNumbeParagraph);
        document.add(details);
        document.add(dctxLogo);

        document.close();

        final File pdfFile = new File(filePath);
        log.debug("saved pdf at {}", pdfFile.getAbsolutePath());
        return pdfFile;
    }
}