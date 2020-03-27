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

import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPassRequest;

import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;


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
     * @param accessPass        AccessPass model
     * @return file object of generated pdf
     */
    public static File generatePdf(String filePath,
                                   File qrCodeFile,
                                   AccessPass accessPass)
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
        Paragraph header = new Paragraph();
        Paragraph details = new Paragraph();

        // checks if pass type is individual or vehicle
        final String passType = accessPass.getPassType();
        if (passType.equals(INDIVIDUAL)) {
            //header
            header.setFontSize(44);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setMarginTop(-50);
            header.setBold();
            header.add(accessPass.getControlCode() + "\n");

            //details
            details.setFontSize(24);
            details.setMarginLeft(70);
            details.add("Access Type:\t" + accessPass.getAporType() + "\n" +
                    "Company:\t" + accessPass.getCompany() + "\n" +
                    "Vallid Until:\t" + accessPass.getValidTo().toString());
        } else if (passType.equals(VEHICLE)) {
            //header
            header.setFontSize(44);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setMarginTop(-50);
            header.setBold();
            header.add(accessPass.getIdentifierNumber() + "\n");

            //details
            details.setFontSize(24);
            details.setMarginLeft(70);
            details.add("Access Type:\t" + accessPass.getAporType() + "\n" +
                    "Company:\t" + accessPass.getCompany() + "\n" +
                    "Vallid Until:\t" + accessPass.getValidTo().toString());
        }

        //writes to the document
        document.add(qrcode);
        document.add(header);
        document.add(details);
        document.add(dctxLogo);

        document.close();

        final File pdfFile = new File(filePath);
        log.debug("saved pdf at {}", pdfFile.getAbsolutePath());
        return pdfFile;
    }
}