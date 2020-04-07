package ph.devcon.rapidpass.services.pdf;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ph.devcon.rapidpass.enums.PassType;
import ph.devcon.rapidpass.models.RapidPass;
import ph.devcon.rapidpass.utilities.DateFormatter;

import java.io.*;
import java.text.ParseException;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;

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

    private static Paragraph generateRapidPassHeader(RapidPass rapidPass) {

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {

            Paragraph header = new Paragraph();
            header.setFontSize(36);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setBold();
            header.setRelativePosition(-100, 25, 0, 0);
            header.add("RAPIDPASS.PH");
            return header;
        } else if (PassType.VEHICLE.equals(rapidPass.getPassType())) {
            Paragraph header = new Paragraph();
            header.setFontSize(54);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setBold();
            header.setRelativePosition(0, 25, 0, 0);
            header.add("RAPIDPASS.PH");
            return header;
        }
        return new Paragraph();

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

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {
            Paragraph header = new Paragraph();
            header.setRelativePosition(150, 420, 0, 0);
            header.setFontSize(36);
            header.setTextAlignment(TextAlignment.LEFT);
            header.setBold();
            header.add(rapidPass.getControlCode());
            return header;
        } else {

            Paragraph header = new Paragraph();
            header.setFixedPosition(220, 90, 350);
            header.setFontSize(44);
            header.setTextAlignment(TextAlignment.LEFT);
            header.setBold();
            header.add(rapidPass.getControlCode());
            return header;
        }



    }

    private static Paragraph[] generateDetails(RapidPass rapidPass, Rectangle rectangle) {

        Paragraph nameParagraph = new Paragraph();
        Paragraph companyParagraph = new Paragraph();
        Paragraph[] results = new Paragraph[2];

        int defaultFontSize = 30;



        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {

            nameParagraph.setMaxWidth(200);
            companyParagraph.setMaxWidth(200);

            defaultFontSize = 12;

            if (rectangle.getY() != 0)
                nameParagraph.setFixedPosition(150, 390, rectangle.getWidth());
            else
                nameParagraph.setFixedPosition(150, 390 - rectangle.getHeight(), rectangle.getWidth());

            nameParagraph.setFixedLeading(20);
            companyParagraph.setFixedPosition(150, 350, rectangle.getWidth());

            if (rectangle.getY() != 0)
                companyParagraph.setFixedPosition(150, 350, rectangle.getWidth());
            else
                companyParagraph.setFixedPosition(150, 350 - rectangle.getHeight(), rectangle.getWidth());
            companyParagraph.setFixedLeading(20);
        } else {
            nameParagraph.setMaxWidth(400);
            companyParagraph.setMaxWidth(400);

            nameParagraph.setFixedPosition(220, 220, 400);
            nameParagraph.setFixedLeading(24);
            companyParagraph.setFixedPosition(220, 160, 400);
            companyParagraph.setFixedLeading(24);
        }

        String name = rapidPass.getName();

        if ("VEHICLE".equals(rapidPass.getPassType().toString())) {

            if ("PLT".equals(rapidPass.getIdType())) {
                name = "PLATE# " + rapidPass.getPlateNumber();
            } else {
                name = "CND: " + rapidPass.getPlateNumber();
            }
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

    private static Paragraph[] generateValidUntil(RapidPass rapidPass, Rectangle rectangle) throws ParseException {

        Instant validUntilInstant = DateFormatter.parse(rapidPass.getValidUntil());
        String validUntil = DateFormatter.readable(validUntilInstant, "MM/dd");

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {
            int defaultFontSize = 14;

            Paragraph details = new Paragraph();
            details.setFontSize(defaultFontSize);
            if (rectangle.getY() != 0)
                details.setFixedPosition(150, 290, rectangle.getWidth());
            else
                details.setFixedPosition(150, 290 - rectangle.getHeight(), rectangle.getWidth());

            details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

            Paragraph date = new Paragraph();
            date.setFontSize(defaultFontSize);
            if (rectangle.getY() != 0)
                date.setFixedPosition(270, 290, rectangle.getWidth());
            else
                date.setFixedPosition(270, 290 - rectangle.getHeight(), rectangle.getWidth());

            date.add(validUntil).setCharacterSpacing(1.3f);
            date.setBold();

            Paragraph[] results = new Paragraph[2];
            results[0] = details;
            results[1] = date;

            return results;
        } else {
            int defaultFontSize = 24;

            Paragraph details = new Paragraph();
            details.setFontSize(defaultFontSize);
            details.setFixedPosition(220, 60, 400);

            details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

            Paragraph date = new Paragraph();
            date.setFontSize(defaultFontSize);
            date.setFixedPosition(400, 60, 200);

            date.add(validUntil).setCharacterSpacing(1.3f);
            date.setBold();

            Paragraph[] results = new Paragraph[2];
            results[0] = details;
            results[1] = date;

            return results;
        }
    }

    /**
     * Enables rendering on a specific area of the document.
     * @param page
     * @param rectangle
     * @param operation
     * @return
     */
    private static PdfCanvas renderOnCanvas(PdfPage page, Rectangle rectangle, Function<PdfCanvas, Consumer<Rectangle>> operation) {

        PdfCanvas canvas = new PdfCanvas(page);

        Consumer<Rectangle> rectangleConsumer = operation.apply(canvas);
        rectangleConsumer.accept(rectangle);

        return canvas;
    }

    private static IBlockElement[] generateAporCode(RapidPass rapidPass, Rectangle rectangle) {

        IBlockElement[] elements = new IBlockElement[3];

        Paragraph passType = new Paragraph();

        String passTypeText = rapidPass.getPassType().toString();
        // Reverted change- they now want INDIVIDUAL to be spelled out. o_o
        // "INDIVIDUAL".equals(rapidPass.getPassType().toString()) ? "PERSON" : rapidPass.getPassType().toString();

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {

            Paragraph aporLabel = new Paragraph();

            passType.add(passTypeText);
            passType.setFontSize(12);
            passType.setFontColor(ColorConstants.WHITE);
            passType.setTextAlignment(TextAlignment.CENTER);
            if (rectangle.getY() != 0)
                passType.setFixedPosition(-205, 400, rectangle.getWidth());
            else
                passType.setFixedPosition(-205, 400 - rectangle.getHeight(), rectangle.getWidth());

            Paragraph aporValue = new Paragraph();
            aporValue.add(rapidPass.getAporType());
            aporValue.setFontSize(40);
            aporValue.setBold();
            aporValue.setFontColor(ColorConstants.WHITE);
            aporValue.setTextAlignment(TextAlignment.CENTER);
            if (rectangle.getY() != 0)
                aporValue.setFixedPosition(-205, 330, rectangle.getWidth());
            else
                aporValue.setFixedPosition(-205, 330 - rectangle.getHeight(), rectangle.getWidth());


            aporLabel.add("APOR");
            aporLabel.setFontSize(24);
            aporLabel.setFontColor(ColorConstants.WHITE);
            aporLabel.setTextAlignment(TextAlignment.CENTER);
            if (rectangle.getY() != 0)
                aporLabel.setFixedPosition(-205, 300, rectangle.getWidth());
            else
                aporLabel.setFixedPosition(-205, 300 - rectangle.getHeight(), rectangle.getWidth());


            elements[0] = aporValue;
            elements[1] = aporLabel;
            elements[2] = passType;

            return elements;

        } else {

            Paragraph aporLabel = new Paragraph();

            passType.add(passTypeText);
            passType.setFontSize(18);
            passType.setFontColor(ColorConstants.WHITE);
            passType.setTextAlignment(TextAlignment.CENTER);
            passType.setFixedPosition(70, 235, 130);


            Paragraph aporValue = new Paragraph();
            aporValue.add(rapidPass.getAporType());
            aporValue.setFontSize(60);
            aporValue.setBold();
            aporValue.setFontColor(ColorConstants.WHITE);
            aporValue.setTextAlignment(TextAlignment.CENTER);
            aporValue.setFixedPosition(70, 140, 130);


            aporLabel.add("APOR");
            aporLabel.setFontSize(32);
            aporLabel.setFontColor(ColorConstants.WHITE);
            aporLabel.setTextAlignment(TextAlignment.CENTER);
            aporLabel.setFixedPosition(70, 110, 130);

            elements[0] = aporValue;
            elements[1] = aporLabel;
            elements[2] = passType;

            return elements;

        }
    }

    /**
     * Generates a pdf at designated file path.
     * <p>
     * Note: We use RapidPass, because AccessPass doesn't directly have easy builders to build with (for testing).
     * Otherwise, we could be using AccessPass as the parameter. In any case, using RapidPass as the POJO  is sufficient.
     * </p>
     *
     * @param qrCodeByteData Image file of the generated QR code
     * @param rapidPass  RapidPass model that contains the details to be printed on the PDF.
     * @return file object of generated pdf
     */
    public OutputStream generatePdf(byte[] qrCodeByteData,
                                    RapidPass rapidPass)
            throws ParseException, IOException {

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType()))
            return generateIndividualPDF(qrCodeByteData, rapidPass);
        else if (PassType.VEHICLE.equals(rapidPass.getPassType()))
            return generateVehiclePDF(qrCodeByteData, rapidPass);

        throw new IllegalArgumentException("Failed to generate PDF. Invalid rapid pass type: " + rapidPass.getPassType());
    }

    private OutputStream generateVehiclePDF(byte[] qrCodeByteData, RapidPass rapidPass) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document document = createDocument(os);

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

        Image instructions = new Image(prepareImage(imageBytes));

        instructions.setFixedPosition(-30, 20);

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.add(instructions);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        PdfPage page2 = document.getPdfDocument().getPage(2);

        // A4 constants
        // http://itext.2136553.n4.nabble.com/java-A4-page-size-is-wrong-in-PageSize-A4-td4659791.html
        float a4PageWidth = 595;
        float a4PageHeight = 842;

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);

        Function<PdfCanvas, Consumer<Rectangle>> generatePdf = canvas -> rectangle -> {

            //noinspection TryWithIdenticalCatches
            try {
                AffineTransform transform = new AffineTransform();
                AffineTransform inverse = transform.createInverse();

                canvas.concatMatrix(transform);

                ImageData imageData = ImageDataFactory.create(qrCodeByteData);

                // Show QR code
                float qrCodeSize = a4PageWidth * 0.95f;
                Point qrPosition = new Point(10, 240);
                canvas.addImage(imageData, new Rectangle((int)qrPosition.getX(), (int) qrPosition.getY(), qrCodeSize, qrCodeSize), false);

                // Show header
                Canvas managedCanvas = new Canvas(canvas, canvas.getDocument(), rectangle);
                Paragraph paragraph = generateRapidPassHeader(rapidPass);

                managedCanvas.add(paragraph);

                managedCanvas.add(generateTitle(rapidPass));

                Paragraph[] details = generateDetails(rapidPass, rectangle);

                managedCanvas.add(details[0]);
                managedCanvas.add(details[1]);

//
                IBlockElement[] iBlockElements = generateAporCode(rapidPass, rectangle);

                canvas.setFillColor(ColorConstants.BLACK);
                canvas.rectangle(new Rectangle(70, 70, 130, 200));
                canvas.fillStroke();

                managedCanvas.add(iBlockElements[0]);
                managedCanvas.add(iBlockElements[1]);
                managedCanvas.add(iBlockElements[2]);

                Paragraph[] paragraphs = generateValidUntil(rapidPass, rectangle);
                managedCanvas.add(paragraphs[0]);
                managedCanvas.add(paragraphs[1]);

                canvas.concatMatrix(inverse);

            } catch (NoninvertibleTransformException | ParseException e) {
                e.printStackTrace();
            }
//            catch (ParseException e) {
//                e.printStackTrace();
//            }

        };

        Rectangle fullPage = new Rectangle(0, 0, a4PageWidth, a4PageHeight);

        renderOnCanvas(page2, fullPage, generatePdf);

        document.close();
        return os;
    }

    private OutputStream generateIndividualPDF(byte[] qrCodeByteData,
                                  RapidPass rapidPass)
            throws ParseException, IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document document = createDocument(os);

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

        Image instructions = new Image(prepareImage(imageBytes));

        instructions.setFixedPosition(-30, 20);

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.add(instructions);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        PdfPage page2 = document.getPdfDocument().getPage(2);

        // A4 constants
        // http://itext.2136553.n4.nabble.com/java-A4-page-size-is-wrong-in-PageSize-A4-td4659791.html
        float a4PageWidth = 595;
        float a4PageHeight = 842;

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);

        Function<PdfCanvas, Consumer<Rectangle>> generatePdf = canvas -> rectangle -> {

            //noinspection TryWithIdenticalCatches
            try {
                AffineTransform transform = new AffineTransform();

                transform.concatenate(
                        AffineTransform.getRotateInstance(Math.PI / 2, rectangle.getX(), rectangle.getY())
                );
                transform.concatenate(
                        AffineTransform.getTranslateInstance(0, -rectangle.getHeight())
                );

                AffineTransform inverse = transform.createInverse();


                canvas.concatMatrix(transform);

                ImageData imageData = ImageDataFactory.create(qrCodeByteData);

                // Show QR code
                float qrCodeSize = a4PageWidth * 0.65f;

                Point qrPosition = new Point(0, rectangle.getY() - 10);
                canvas.addImage(imageData, new Rectangle((int)qrPosition.getX(), (int) qrPosition.getY(), qrCodeSize, qrCodeSize), false);

                // Show header
                Canvas managedCanvas = new Canvas(canvas, canvas.getDocument(), rectangle);
                Paragraph paragraph = generateRapidPassHeader(rapidPass);
                managedCanvas.add(paragraph);

                managedCanvas.add(generateTitle(rapidPass));

                Paragraph[] details = generateDetails(rapidPass, rectangle);

                managedCanvas.add(details[0]);
                managedCanvas.add(details[1]);

//
                IBlockElement[] iBlockElements = generateAporCode(rapidPass, rectangle);

                canvas.setFillColor(ColorConstants.BLACK);
                canvas.rectangle(new Rectangle(rectangle.getX() + 45, rectangle.getY() - 130, 90, 135));
                canvas.fillStroke();

                managedCanvas.add(iBlockElements[0]);
                managedCanvas.add(iBlockElements[1]);
                managedCanvas.add(iBlockElements[2]);

                Paragraph[] paragraphs = generateValidUntil(rapidPass, rectangle);
                managedCanvas.add(paragraphs[0]);
                managedCanvas.add(paragraphs[1]);

                canvas.concatMatrix(inverse);

            } catch (NoninvertibleTransformException | ParseException e) {
                e.printStackTrace();
            }
//            catch (ParseException e) {
//                e.printStackTrace();
//            }

        };

        Rectangle leftCopy = new Rectangle(0, 0, a4PageWidth, a4PageHeight / 2);
        Rectangle rightCopy = new Rectangle(0, a4PageHeight / 2, a4PageWidth, a4PageHeight / 2);

        renderOnCanvas(page2, leftCopy, generatePdf);
        renderOnCanvas(page2, rightCopy, generatePdf);

        document.close();
        return os;
    }

}