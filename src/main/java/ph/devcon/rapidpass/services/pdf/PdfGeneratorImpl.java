package ph.devcon.rapidpass.services.pdf;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.utilities.DateFormatter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

/**
 * Utility class for generating Pdf using File qrcode file from QRCode Generator.
 */
@Slf4j
@Service
public class PdfGeneratorImpl implements PdfGeneratorService {

    /**
     * Path to Work Sans Font
     */
    private static final String WORK_SANS = "src/main/resources/fonts/WorkSans-VariableFont_wght.ttf";

    public PdfGeneratorImpl() {
        // noop
    }

    /**
     * Creates the pdf document and set its properties.
     *
     * @param os output stream to use in creating document
     * @return PDF document
     * @throws FileNotFoundException error creating file.
     * @throws IOException
     */
    private static Document createDocument(OutputStream os) throws FileNotFoundException, IOException {

        PdfDocument pdfdocument = new PdfDocument(new PdfWriter(os));

        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
//        document.setFont(prepareFont()); FIXME
        document.setMargins(-50, -50, -50, -50);

        return document;
    }

    /**
     * Prepare image.
     *
     * @return image data
     */
    private static ImageData prepareImage(byte[] data) {
        log.debug("preparingImage");

        // get image from resource classpath
        return ImageDataFactory.create(data);
    }

    /**
     * Prepare the font
     *
     * @return font program
     * @throws IOException
     */
    private static PdfFont prepareFont() throws IOException {
        log.debug("preparing font from {}", WORK_SANS);

        ClassPathResource instructionsClassPath = new ClassPathResource("fonts/WorkSans-VariableFont_wght.ttf");
        byte[] fontBytes = toByteArray(instructionsClassPath.getInputStream());

        // Prepare the font
        final FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);

        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, true);

        return font;
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    private static Paragraph generateRapidPassHeader() {

        Paragraph header = new Paragraph();
        header.setFontSize(56);
        header.setTextAlignment(TextAlignment.CENTER);
        header.setBold();
        header.setFixedPosition(50, 730, 500);
        header.add("RAPIDPASS.PH");
        return header;

    }

    private static Image generateQrCode(byte[] qrCodeImage) throws IOException {
        //qrcode image
        Image qrcode = new Image(prepareImage(qrCodeImage));

        qrcode.setFixedPosition(0, 200);
        qrcode.setWidth(590);
        qrcode.setHeight(590);

        return qrcode;
    }

    private static Paragraph generateTitle(RapidPass rapidPass) {

        Paragraph header = new Paragraph();
        header.setFixedPosition(220, 45, 350);
        header.setFontSize(54);
        header.setTextAlignment(TextAlignment.LEFT);
        header.setMarginTop(-50);
        header.setBold();
        header.add(rapidPass.getControlCode());

        return header;

    }

    private static Paragraph[] generateDetails(RapidPass rapidPass) {

        Paragraph nameParagraph = new Paragraph();
        Paragraph companyParagraph = new Paragraph();
        Paragraph[] results = new Paragraph[2];

        int defaultFontSize = 21;

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {
            nameParagraph.setFixedPosition(220, 175, 340);
            nameParagraph.setFixedLeading(24);
            companyParagraph.setFixedPosition(220, 120, 340);
            companyParagraph.setFixedLeading(26);
        } else if (PassType.VEHICLE.equals(rapidPass.getPassType())) {
            nameParagraph.setFixedPosition(220, 180, 340);
            nameParagraph.setFixedLeading(24);
            companyParagraph.setFixedPosition(220, 125, 340);
            companyParagraph.setFixedLeading(26);
        }

        String name = rapidPass.getName();

        if ("VEHICLE".equals(rapidPass.getPassType().toString())) {
            name = "PLATE# " + rapidPass.getPlateNumber();
        }

        String company = rapidPass.getCompany();

        nameParagraph.add(name);
        companyParagraph.add(company);

        nameParagraph.setFontSize(defaultFontSize);
        companyParagraph.setFontSize(defaultFontSize);

        results[0] = nameParagraph;
        results[1] = companyParagraph;

        return results;
    }

    private static Paragraph[] generateValidUntil(RapidPass rapidPass) throws ParseException {
        SimpleDateFormat formatToPdf = new SimpleDateFormat("MM/dd");

        Instant validUntilInstant = DateFormatter.parse(rapidPass.getValidUntil());
        String validUntil = DateFormatter.readable(validUntilInstant, "MM/dd");

        int defaultFontSize = 24;

        Paragraph details = new Paragraph();
        details.setFontSize(defaultFontSize);
        details.setFixedPosition(220, 25, 230);
        details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

        Paragraph date = new Paragraph();
        date.setFontSize(defaultFontSize);
        date.setFixedPosition(400, 25, 230);
        date.add(validUntil).setCharacterSpacing(1.3f);
        date.setBold();

        Paragraph[] results = new Paragraph[2];
        results[0] = details;
        results[1] = date;

        return results;
    }

    private static IBlockElement[] generateAporCode(RapidPass rapidPass, Document document) {

        IBlockElement[] elements = new IBlockElement[3];

        Paragraph aporLabel = new Paragraph();
        aporLabel.add("APOR");
        aporLabel.setFontSize(42);
        aporLabel.setFontColor(ColorConstants.WHITE);
        aporLabel.setTextAlignment(TextAlignment.CENTER);
        aporLabel.setFixedPosition(30, 40, 170);

        Paragraph aporValue = new Paragraph();
        aporValue.add(rapidPass.getAporType());
        aporValue.setFontSize(90);
        aporValue.setFontColor(ColorConstants.WHITE);
        aporValue.setTextAlignment(TextAlignment.CENTER);
        aporValue.setFixedPosition(30, 70, 170);

        Paragraph passType = new Paragraph();

        String passTypeText = "INDIVIDUAL".equals(rapidPass.getPassType().toString()) ? "PERSON" : rapidPass.getPassType().toString();

        passType.add(passTypeText);
        passType.setFontSize(26);
        passType.setFontColor(ColorConstants.WHITE);
        passType.setTextAlignment(TextAlignment.CENTER);
        passType.setFixedPosition(30, 190, 170);


        PdfCanvas canvas = new PdfCanvas(document.getPdfDocument().getPage(2));
        Rectangle rectangle = new Rectangle(30, 30, 170, 210);
        canvas.setFillColor(ColorConstants.BLACK);
        canvas.rectangle(rectangle);
        canvas.fillStroke();

        elements[0] = aporValue;
        elements[1] = aporLabel;
        elements[2] = passType;

        return elements;
    }

    /**
     * Generates a pdf at designated file path.
     * <p>
     * Note: We use RapidPass, because AccessPass doesn't directly have easy builders to build with (for testing).
     * Otherwise, we could be using AccessPass as the parameter. In any case, using RapidPass as the POJO  is sufficient.
     *
     * @param qrCodeFile Image file of the generated QR code
     * @param rapidPass  RapidPass model that contains the details to be printed on the PDF.
     * @return file object of generated pdf
     */
    public OutputStream generatePdf(byte[] qrCodeFile,
                                    RapidPass rapidPass)
            throws ParseException, IOException {

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document document = createDocument(os);

        document.setFont(prepareFont());
        document.setMargins(-50, -50, -50, -50);

        ClassPathResource instructionsClassPath;

        switch (rapidPass.getPassType()) {
            case INDIVIDUAL:
                instructionsClassPath = new ClassPathResource("i-instructions.png");
                break;
            case VEHICLE:
                instructionsClassPath = new ClassPathResource("v-instructions.png");
                break;
            default:
                throw new IllegalStateException("Failed to determine pass type.");
        }

        InputStream inputStream = instructionsClassPath.getInputStream();
        byte[] imageBytes = toByteArray(inputStream);

        Image instructions = new Image(prepareImage(imageBytes)).scale(0.9f, 0.9f);

        instructions.setFixedPosition(0, 20);

        document.add(instructions);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        Image qrcode = generateQrCode(qrCodeFile);
        //processes the data that will be on the pdf

        //writes to the document

        document.add(qrcode);
        document.add(generateRapidPassHeader());
        document.add(generateTitle(rapidPass));

        Paragraph[] details = generateDetails(rapidPass);

        document.add(details[0]);
        document.add(details[1]);

        Paragraph[] paragraphs = generateValidUntil(rapidPass);
        document.add(paragraphs[0]);
        document.add(paragraphs[1]);

        IBlockElement[] iBlockElements = generateAporCode(rapidPass, document);
        document.add(iBlockElements[0]);
        document.add(iBlockElements[1]);
        document.add(iBlockElements[2]);

        document.close();
        return os;
    }

}