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

package ph.devcon.rapidpass.services.pdf;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
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


    // A4 constants
    // http://itext.2136553.n4.nabble.com/java-A4-page-size-is-wrong-in-PageSize-A4-td4659791.html
    private static final float A4_PAGE_WIDTH = 595;
    private static final float A4_PAGE_HEIGHT = 842;

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

    private static Paragraph generateRapidPassHeader(RapidPass rapidPass, Rectangle rectangle) {

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {

            Paragraph header = new Paragraph();
            header.setFontSize(54);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setBold();
            header.setFixedPosition(rectangle.getX(), rectangle.getHeight() + 310, A4_PAGE_WIDTH);
            header.add("RAPIDPASS.PH");
            return header;
        } else if (PassType.VEHICLE.equals(rapidPass.getPassType())) {
            Paragraph header = new Paragraph();
            header.setFontSize(78);
            header.setTextAlignment(TextAlignment.CENTER);
            header.setBold();
            header.setCharacterSpacing(1.3f);
            header.setFixedPosition(120, A4_PAGE_HEIGHT + 150, rectangle.getWidth());
            header.add("RAPIDPASS.PH");
            return header;
        }
        return new Paragraph();

    }

    private static Paragraph generateTitle(RapidPass rapidPass, Rectangle rectangle) {

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {
            Paragraph header = new Paragraph();
            header.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 120, rectangle.getWidth());
            header.setFontSize(36);
            header.setTextAlignment(TextAlignment.LEFT);
            header.setBold();
            header.add(rapidPass.getControlCode());
            return header;
        } else {

            Paragraph header = new Paragraph();
            header.setFixedPosition(290, 150, 350);
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

            nameParagraph.setMaxWidth(rectangle.getWidth());
            companyParagraph.setMaxWidth(rectangle.getWidth());

            defaultFontSize = 20;

            nameParagraph.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 230, A4_PAGE_WIDTH);
            nameParagraph.setFixedLeading(20);

            companyParagraph.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 180, A4_PAGE_WIDTH);
            companyParagraph.setFixedLeading(20);
        } else {
            nameParagraph.setMaxWidth(400);
            companyParagraph.setMaxWidth(400);

            nameParagraph.setFixedPosition(290, 290, 400);
            nameParagraph.setFixedLeading(24);
            companyParagraph.setFixedPosition(290, 230, 400);
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

        String originCity = StringUtils.defaultIfBlank(rapidPass.getOriginCity(), "NA");
        String destCity = StringUtils.defaultIfBlank(rapidPass.getDestCity(), "NA");


        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType())) {
            int defaultFontSize = 16;

            Paragraph details = new Paragraph();
            details.setFontSize(defaultFontSize);
            details.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 100, rectangle.getWidth());

            details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

            Paragraph date = new Paragraph();
            date.setFontSize(defaultFontSize);
            date.setFixedPosition(rectangle.getX() + 230 + 120, rectangle.getY() + 100, rectangle.getWidth());

            date.add(validUntil).setCharacterSpacing(1.3f);
            date.setBold();

            // Add from - to

            Paragraph originToDest = new Paragraph();
            originToDest.setFontSize(defaultFontSize);
            originToDest.setTextAlignment(TextAlignment.LEFT);
            originToDest.add(String.format("%s - %s", originCity, destCity)).setCharacterSpacing(1.3f);

            originToDest.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 70, rectangle.getWidth());


            Paragraph tampering_is_illegal = new Paragraph();
            tampering_is_illegal.setFontSize(10);
            tampering_is_illegal.setItalic();
            tampering_is_illegal.setTextAlignment(TextAlignment.LEFT);
            tampering_is_illegal.add("Falsification of this Pass is a criminal offense.").setCharacterSpacing(1.5f);
            tampering_is_illegal.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 55, rectangle.getWidth());

            Paragraph[] results = new Paragraph[4];
            results[0] = details;
            results[1] = date;
            results[2] = originToDest;
            results[3] = tampering_is_illegal;

            return results;
        } else {
            int defaultFontSize = 24;

            Paragraph details = new Paragraph();
            details.setFontSize(defaultFontSize);
            details.setFixedPosition(290, 120, 400);

            details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

            Paragraph date = new Paragraph();
            date.setFontSize(defaultFontSize);
            date.setFixedPosition(470, 120, 200);

            date.add(validUntil).setCharacterSpacing(1.3f);
            date.setBold();

            Paragraph originToDest = new Paragraph();
            originToDest.setFontSize(defaultFontSize);
            originToDest.setMaxWidth(rectangle.getHeight());
            originToDest.setTextAlignment(TextAlignment.LEFT);
            originToDest.add(String.format("%s - %s", originCity, destCity)).setCharacterSpacing(1.3f);
            originToDest.setFixedPosition(290, 90, 400);

            Paragraph tampering_is_illegal = new Paragraph();
            tampering_is_illegal.setFontSize(16);
            tampering_is_illegal.setItalic();
            tampering_is_illegal.setTextAlignment(TextAlignment.LEFT);
            tampering_is_illegal.add("Tampering with the pass is punishable by law.").setCharacterSpacing(1.5f);
            tampering_is_illegal.setFixedPosition(290, 75, 400);


            Paragraph[] results = new Paragraph[4];
            results[0] = details;
            results[1] = date;
            results[2] = originToDest;
            results[3] = tampering_is_illegal;

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
            passType.setFontSize(20);
            passType.setFontColor(ColorConstants.WHITE);
            passType.setTextAlignment(TextAlignment.CENTER);
            passType.setFixedPosition(rectangle.getX(), rectangle.getY() + 220 + 10, rectangle.getWidth());

            Paragraph aporValue = new Paragraph();
            aporValue.add(rapidPass.getAporType());
            aporValue.setFontSize(60);
            aporValue.setBold();
            aporValue.setFontColor(ColorConstants.WHITE);
            aporValue.setTextAlignment(TextAlignment.CENTER);
            aporValue.setFixedPosition(rectangle.getX(), rectangle.getY() + 130 + 10, rectangle.getWidth());


            aporLabel.add("APOR");
            aporLabel.setFontSize(30);
            aporLabel.setFontColor(ColorConstants.WHITE);
            aporLabel.setTextAlignment(TextAlignment.CENTER);
            aporLabel.setFixedPosition(rectangle.getX(), rectangle.getY() + 100 + 10, rectangle.getWidth());


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
            passType.setFixedPosition(110, 270, 130);


            Paragraph aporValue = new Paragraph();
            aporValue.add(rapidPass.getAporType());
            aporValue.setFontSize(72);
            aporValue.setBold();
            aporValue.setFontColor(ColorConstants.WHITE);
            aporValue.setTextAlignment(TextAlignment.CENTER);
            aporValue.setFixedPosition(110, 165, 130);


            aporLabel.add("APOR");
            aporLabel.setFontSize(40);
            aporLabel.setFontColor(ColorConstants.WHITE);
            aporLabel.setTextAlignment(TextAlignment.CENTER);
            aporLabel.setFixedPosition(110, 130, 130);

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

        instructions.scale(0.65f, 0.65f);
        instructions.setFixedPosition(0, A4_PAGE_HEIGHT);

        instructions.setRotationAngle(-Math. PI / 2);

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.add(instructions);

        PdfPage firstPage = document.getPdfDocument().getFirstPage();

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);

        Function<PdfCanvas, Consumer<Rectangle>> generatePdf = canvas -> rectangle -> {

            //noinspection TryWithIdenticalCatches
            try {
                AffineTransform transform = new AffineTransform();

                transform.concatenate(
                        AffineTransform.getRotateInstance(-Math.PI / 2, rectangle.getX(), rectangle.getY())
                );
                transform.concatenate(
                        AffineTransform.getTranslateInstance(rectangle.getX(), rectangle.getY())
                );
                transform.concatenate(
                        AffineTransform.getScaleInstance(0.5f, 0.5f)
                );

                AffineTransform inverse = transform.createInverse();

                canvas.concatMatrix(transform);

                ImageData imageData = ImageDataFactory.create(qrCodeByteData);

                // Show QR code
                float qrCodeSize = A4_PAGE_HEIGHT * 1f;
                Point qrPosition = new Point(rectangle.getX(), rectangle.getY() - 170);
                canvas.addImage(imageData, new Rectangle((int)qrPosition.getX(), (int) qrPosition.getY(), qrCodeSize, qrCodeSize), true);

                // Show header
                Canvas managedCanvas = new Canvas(canvas, canvas.getDocument(), rectangle);
                Paragraph paragraph = generateRapidPassHeader(rapidPass, rectangle);

                managedCanvas.add(paragraph);

                managedCanvas.add(generateTitle(rapidPass, rectangle));

                Paragraph[] details = generateDetails(rapidPass, rectangle);

                managedCanvas.add(details[0]);
                managedCanvas.add(details[1]);

//
                IBlockElement[] iBlockElements = generateAporCode(rapidPass, rectangle);

                float aporWidth = rectangle.getWidth() * 0.25f;
                float aporHeight = rectangle.getHeight() * 0.26f;
                float aporMargin = 100;

                canvas.setFillColor(ColorConstants.BLACK);
                canvas.rectangle(new Rectangle(0 + aporMargin, 0 + aporMargin, aporWidth, aporHeight));
                canvas.fillStroke();

                managedCanvas.add(iBlockElements[0]);
                managedCanvas.add(iBlockElements[1]);
                managedCanvas.add(iBlockElements[2]);

                Paragraph[] paragraphs = generateValidUntil(rapidPass, rectangle);
                managedCanvas.add(paragraphs[0]);
                managedCanvas.add(paragraphs[1]);
                managedCanvas.add(paragraphs[2]);
                managedCanvas.add(paragraphs[3]);

                canvas.concatMatrix(inverse);

            } catch (NoninvertibleTransformException | ParseException e) {
                e.printStackTrace();
            }
//            catch (ParseException e) {
//                e.printStackTrace();
//            }

        };

        Rectangle halfPage = new Rectangle(0, A4_PAGE_HEIGHT / 2, A4_PAGE_WIDTH, A4_PAGE_HEIGHT);

        renderOnCanvas(firstPage, halfPage, generatePdf);


        PdfCanvas pdfCanvas = new PdfCanvas(firstPage);
        pdfCanvas.moveTo(0, A4_PAGE_HEIGHT / 2)
                .setStrokeColor(new DeviceRgb(0.7f, 0.7f, 0.7f))
                .setLineWidth(1)
                .lineTo(A4_PAGE_WIDTH, A4_PAGE_HEIGHT / 2)
                .closePathStroke();

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

        instructions.scale(0.65f, 0.65f);
        instructions.setFixedPosition(0, A4_PAGE_HEIGHT);

        instructions.setRotationAngle(-Math. PI / 2);
//
        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.add(instructions);

        PdfPage firstPage = document.getPdfDocument().getFirstPage();

        Canvas wholeCanvas = new Canvas(firstPage, new Rectangle(0, 0, A4_PAGE_WIDTH, A4_PAGE_HEIGHT));

        document.getPdfDocument().setDefaultPageSize(PageSize.A4);

        Function<PdfCanvas, Consumer<Rectangle>> generatePdf = canvas -> rectangle -> {

            //noinspection TryWithIdenticalCatches
            try {
                AffineTransform transform = new AffineTransform();

//                transform.concatenate(
//                        AffineTransform.getRotateInstance(Math.PI / 2, rectangle.getX(), rectangle.getY())
//                );
                transform.concatenate(
                        AffineTransform.getTranslateInstance(rectangle.getX() / 2, rectangle.getY())
                );
                transform.concatenate(
                        AffineTransform.getScaleInstance(0.5f, 0.5f)
                );

                AffineTransform inverse = transform.createInverse();


                canvas.concatMatrix(transform);

                ImageData imageData = ImageDataFactory.create(qrCodeByteData);

                // Show QR code
                float qrCodeSize = A4_PAGE_WIDTH * 0.9f;

                Point qrPosition = new Point(rectangle.getX() + 20,  rectangle.getY() + 240);
                canvas.addImage(imageData, new Rectangle((int)qrPosition.getX(), (int) qrPosition.getY(), qrCodeSize, qrCodeSize), true);

                // Show header
                Canvas managedCanvas = new Canvas(canvas, canvas.getDocument(), rectangle);
                Paragraph paragraph = generateRapidPassHeader(rapidPass, rectangle);
                managedCanvas.add(paragraph);

                managedCanvas.add(generateTitle(rapidPass, rectangle));

                Paragraph[] details = generateDetails(rapidPass, rectangle);

                managedCanvas.add(details[0]);
                managedCanvas.add(details[1]);

//
                IBlockElement[] iBlockElements = generateAporCode(rapidPass, rectangle);

                float aporWidth = rectangle.getWidth() * 0.5f;
                float aporHeight = rectangle.getHeight() * 0.5f;
                float aporMargin = 70;

                canvas.setFillColor(ColorConstants.BLACK);
                canvas.rectangle(new Rectangle(rectangle.getX() + aporMargin, rectangle.getY() + aporMargin, aporWidth, aporHeight));
                canvas.fillStroke();

                managedCanvas.add(iBlockElements[0]);
                managedCanvas.add(iBlockElements[1]);
                managedCanvas.add(iBlockElements[2]);

                Paragraph[] paragraphs = generateValidUntil(rapidPass, rectangle);
                managedCanvas.add(paragraphs[0]);
                managedCanvas.add(paragraphs[1]);
                managedCanvas.add(paragraphs[2]);
                managedCanvas.add(paragraphs[3]);

                canvas.concatMatrix(inverse);

            } catch (NoninvertibleTransformException | ParseException e) {
                e.printStackTrace();
            }
//            catch (ParseException e) {
//                e.printStackTrace();
//            }

        };

        Rectangle leftCopy = new Rectangle(0, 0, A4_PAGE_WIDTH / 2, A4_PAGE_HEIGHT / 2);
        Rectangle rightCopy = new Rectangle(A4_PAGE_WIDTH / 2, 0, A4_PAGE_WIDTH / 2, A4_PAGE_HEIGHT / 2);

        renderOnCanvas(firstPage, leftCopy, generatePdf);
        renderOnCanvas(firstPage, rightCopy, generatePdf);


        PdfCanvas pdfCanvas = new PdfCanvas(firstPage);
        pdfCanvas.moveTo(0, A4_PAGE_HEIGHT / 2)
                .setStrokeColor(new DeviceRgb(0.7f, 0.7f, 0.7f))
                .setLineWidth(1)
                .lineTo(A4_PAGE_WIDTH, A4_PAGE_HEIGHT / 2)
                .closePathStroke()
                .moveTo(A4_PAGE_WIDTH / 2, A4_PAGE_HEIGHT / 2)
                .setStrokeColor(new DeviceRgb(0.7f, 0.7f, 0.7f))
                .setLineWidth(1)
                .lineTo(A4_PAGE_WIDTH / 2, 0)
                .closePathStroke()
        ;


        document.close();
        return os;
    }

}