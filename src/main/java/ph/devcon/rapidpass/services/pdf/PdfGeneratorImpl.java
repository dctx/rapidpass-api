package ph.devcon.rapidpass.services.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;
import ph.devcon.rapidpass.entities.AccessPass;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.models.RapidPassRequest;

import static ph.devcon.rapidpass.enums.PassType.INDIVIDUAL;
import static ph.devcon.rapidpass.enums.PassType.VEHICLE;


/**
 * Utility class for generating Pdf using File qrcode file from QRCode Generator.
 */
@Slf4j
public class PdfGeneratorImpl implements PdfGeneratorService {


    /**
     * Path to the logo that will be appended to the PDF.
     */
    private static final String DCTX_LOGO_PATH = "light-bg.png";


    public PdfGeneratorImpl() {
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

    private static Paragraph generateRapidPassHeader() {
        Paragraph header = new Paragraph();
        header.setFontSize(72);
        header.setTextAlignment(TextAlignment.CENTER);
        header.setBold();
        header.setFixedPosition(50, 720, 500);
        header.add("RAPIDPASS");
        return header;

    }

    private static Image generateQrCode(File file) throws MalformedURLException {

        //qrcode image
        Image qrcode = new Image(prepareImage(file.getAbsolutePath()));

        qrcode.setFixedPosition(0, 200);
        qrcode.setWidth(590);
        qrcode.setHeight(590);

        return qrcode;
    }

    private static Paragraph generateTitle(RapidPass rapidPass) {

        Paragraph header = new Paragraph();
        header.setFixedPosition(20, 60, 500);
        header.setFontSize(54);
        header.setTextAlignment(TextAlignment.LEFT);
        header.setMarginTop(-50);
        header.setBold();

        // checks if pass type is individual or vehicle
        final String passType = rapidPass.getPassType().toLowerCase();
        if (passType.equals("individual")) {
            header.add(rapidPass.getControlCode() + "\n");

        } else if (passType.equals("vehicle")) {
            header.add(rapidPass.getIdentifierNumber() + "\n");
        }

        return header;

    }

    private static Paragraph generateDetails(RapidPass rapidPass) {

        Paragraph details = new Paragraph();
        details.setFontSize(26);
        details.setMarginLeft(70);
        details.setFixedPosition(20, 140, 500);

        // checks if pass type is individual or vehicle
        final String passType = rapidPass.getPassType().toLowerCase();
        if (passType.equals("individual")) {

            String name = rapidPass.getName();
            String company = rapidPass.getCompany();

            details.add(name + "\n" + company);

        } else if (passType.equals("vehicle")) {

            String name = rapidPass.getName();
            String company = rapidPass.getCompany();

            details.add(name + "\n" + company);
        }
        return details;
    }

    private static Paragraph[] generateValidUntil(RapidPass rapidPass) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");

        String validUntil = sdf.format(rapidPass.getValidTo());

        Paragraph details = new Paragraph();
        details.setFontSize(24);
        details.setFixedPosition(20, 10, 250);
        details.add("EXPIRES");

        Paragraph date = new Paragraph();
        date.setFontSize(44);
        date.setFixedPosition(130, 0, 500);
        date.add(validUntil);

        Paragraph[] results = new Paragraph[2];
        results[0] = details;
        results[1] = date;

        return results;
    }

    private static IBlockElement[] generateAporCode(RapidPass rapidPass, Document document) {

        IBlockElement[] elements = new IBlockElement[2];

        Paragraph aporLabel = new Paragraph();
        aporLabel.add("APOR");
        aporLabel.setFontSize(42);
        aporLabel.setFontColor(ColorConstants.WHITE);
        aporLabel.setTextAlignment(TextAlignment.CENTER);
        aporLabel.setFixedPosition(420, 40, 170);

        Paragraph aporValue = new Paragraph();
        aporValue.add("NR");
        aporValue.setFontSize(90);
        aporValue.setFontColor(ColorConstants.WHITE);
        aporValue.setTextAlignment(TextAlignment.CENTER);
        aporValue.setFixedPosition(420, 60, 170);


        PdfCanvas canvas = new PdfCanvas(document.getPdfDocument().getFirstPage());
        Rectangle rectangle = new Rectangle(420, 5, 170, 210);
        canvas.setFillColor(ColorConstants.BLACK);
        canvas.rectangle(rectangle);
        canvas.fillStroke();

        elements[0] = aporValue;
        elements[1] = aporLabel;

        return elements;
    }

    /**
     * Generates a pdf at designated file path.
     *
     * Note: We use RapidPass, because AccessPass doesn't directly have easy builders to build with (for testing).
     * Otherwise, we could be using AccessPass as the parameter. In any case, using RapidPass as the POJO  is sufficient.
     *
     * @param filePath          path of file where to save pdf
     * @param qrCodeFile        Image file of the generated QR code
     * @param rapidPass         RapidPass model that contains the details to be printed on the PDF.
     * @return file object of generated pdf
     */
    public File generatePdf(String filePath,
                                   File qrCodeFile,
                                   RapidPass rapidPass)
            throws FileNotFoundException, MalformedURLException {
        log.debug("generating pdf at {}", filePath);

        Document document = createDocument(filePath);

        Image qrcode = generateQrCode(qrCodeFile);

        // get dctx logo from classpath resource
        Image dctxLogo = new Image(prepareImage(
                ResourceUtils.getFile("classpath:light-bg.png")
                        .getAbsolutePath()));
        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400, 0);

        //processes the data that will be on the pdf



        //writes to the document

        document.add(qrcode);
        document.add(generateRapidPassHeader());
        document.add(generateTitle(rapidPass));
        document.add(generateDetails(rapidPass));

        Paragraph[] paragraphs = generateValidUntil(rapidPass);
        document.add(paragraphs[0]);
        document.add(paragraphs[1]);

        IBlockElement[] iBlockElements = generateAporCode(rapidPass, document);
        document.add(iBlockElements[0]);
        document.add(iBlockElements[1]);
//        document.add(dctxLogo);

        document.close();

        final File pdfFile = new File(filePath);
        log.debug("saved pdf at {}", pdfFile.getAbsolutePath());
        return pdfFile;
    }
}