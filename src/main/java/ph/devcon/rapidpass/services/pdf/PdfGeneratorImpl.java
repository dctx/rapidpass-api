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

        Paragraph header = new Paragraph();
        header.setFontSize(50);
        header.setTextAlignment(TextAlignment.CENTER);
        header.setBold();
        header.setFixedPosition(rectangle.getX(), rectangle.getHeight() + 310, A4_PAGE_WIDTH);
        header.add("IATF RAPIDPASS");
        return header;
    }

    private static Paragraph generatePersonsName(RapidPass rapidPass, Rectangle rectangle) {

        Paragraph nameParagraph = new Paragraph();

        nameParagraph.setMaxWidth(rectangle.getWidth());

        nameParagraph.setFixedPosition(rectangle.getX(), rectangle.getY() + 60, A4_PAGE_WIDTH);
        nameParagraph.setWidth(A4_PAGE_WIDTH);
        nameParagraph.setMaxWidth(A4_PAGE_WIDTH);
        nameParagraph.setFixedLeading(20);
        nameParagraph.add(rapidPass.getName());
        nameParagraph.setFontSize(20);
        nameParagraph.setTextAlignment(TextAlignment.CENTER);
        nameParagraph.setFontColor(ColorConstants.WHITE);

        return nameParagraph;
    }


    private static Paragraph[] generateDetails(RapidPass rapidPass, Rectangle rectangle) {

        Paragraph nameParagraph = new Paragraph();
        Paragraph companyParagraph = new Paragraph();
        Paragraph[] results = new Paragraph[2];

        nameParagraph.setMaxWidth(rectangle.getWidth());
        companyParagraph.setMaxWidth(rectangle.getWidth());

        int defaultFontSize = 22;

        nameParagraph.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 230, A4_PAGE_WIDTH);
        nameParagraph.setFixedLeading(25);

        companyParagraph.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 200, A4_PAGE_WIDTH);
        companyParagraph.setFixedLeading(25);
        companyParagraph.setCharacterSpacing(1.5f);

        String name = rapidPass.getName();

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

        int defaultFontSize = 20;

        Paragraph details = new Paragraph();
        details.setFontSize(defaultFontSize);
        details.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 130, rectangle.getWidth());

        details.add("VALID UNTIL: ").setCharacterSpacing(1.3f);

        Paragraph date = new Paragraph();
        date.setFontSize(defaultFontSize);
        date.setFixedPosition(rectangle.getX() + 230 + 170, rectangle.getY() + 130, rectangle.getWidth());

        date.add(validUntil).setCharacterSpacing(1.3f);
        date.setBold();

        // Add from - to

        Paragraph originToDest = new Paragraph();
        originToDest.setFontSize(defaultFontSize - 2);
        originToDest.setTextAlignment(TextAlignment.LEFT);
        originToDest.add(String.format("%s - %s", originCity, destCity)).setCharacterSpacing(1.2f);

        originToDest.setFixedPosition(rectangle.getX() + 230, rectangle.getY() + 160, rectangle.getWidth());


        Paragraph tampering_is_illegal = new Paragraph();
        tampering_is_illegal.setFontSize(10);
        tampering_is_illegal.setItalic();
        tampering_is_illegal.setTextAlignment(TextAlignment.CENTER);

        String WARNING = "Falsification of this Pass is a criminal offense.".toUpperCase();

        tampering_is_illegal.add(WARNING).setCharacterSpacing(1.5f);
        tampering_is_illegal.setFixedPosition(rectangle.getX(), rectangle.getY() + 20, rectangle.getWidth() * 2);
        tampering_is_illegal.setMaxWidth(rectangle.getWidth() * 2);

        Paragraph[] results = new Paragraph[4];
        results[0] = details;
        results[1] = date;
        results[2] = originToDest;
        results[3] = tampering_is_illegal;

        return results;

    }

    /**
     * Enables rendering on a specific area of the document.
     *
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

        Paragraph aporLabel = new Paragraph();

        Paragraph aporValue = new Paragraph();

        aporValue.add(rapidPass.getAporType());
        aporValue.setFontSize(60);
        aporValue.setBold();
        aporValue.setFontColor(ColorConstants.BLACK);
        aporValue.setTextAlignment(TextAlignment.CENTER);
        aporValue.setFixedPosition(rectangle.getX(), rectangle.getY() + 170 + 10, rectangle.getWidth());

        aporLabel.add("APOR");
        aporLabel.setFontSize(30);
        aporLabel.setFontColor(ColorConstants.BLACK);
        aporLabel.setTextAlignment(TextAlignment.CENTER);
        aporLabel.setFixedPosition(rectangle.getX(), rectangle.getY() + 130 + 10, rectangle.getWidth());


        elements[0] = aporValue;
        elements[1] = aporLabel;

        return elements;

    }

    /**
     * Generates a pdf at designated file path.
     * <p>
     * Note: We use RapidPass, because AccessPass doesn't directly have easy builders to build with (for testing).
     * Otherwise, we could be using AccessPass as the parameter. In any case, using RapidPass as the POJO  is sufficient.
     * </p>
     *
     * @param qrCodeByteData Image file of the generated QR code
     * @param rapidPass      RapidPass model that contains the details to be printed on the PDF.
     * @return file object of generated pdf
     */
    public OutputStream generatePdf(byte[] qrCodeByteData,
                                    RapidPass rapidPass)
            throws ParseException, IOException {

        if (PassType.INDIVIDUAL.equals(rapidPass.getPassType()))
            return generateIndividualPDF(qrCodeByteData, rapidPass);

        throw new IllegalArgumentException("Failed to generate PDF. Invalid rapid pass type: " + rapidPass.getPassType());
    }

    private OutputStream generateIndividualPDF(byte[] qrCodeByteData,
                                               RapidPass rapidPass)
            throws ParseException, IOException {


        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document document = createDocument(os);

        document.setMargins(-50, -50, -50, -50);

        ClassPathResource instructionsClassPath = new ClassPathResource("i-instructions.png");

        InputStream inputStream = instructionsClassPath.getInputStream();
        byte[] imageBytes = toByteArray(inputStream);

        Image instructions = new Image(prepareImage(imageBytes));

        instructions.scale(0.65f, 0.65f);
        instructions.setFixedPosition(0, A4_PAGE_HEIGHT);

        instructions.setRotationAngle(-Math.PI / 2);
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

                Point qrPosition = new Point(rectangle.getX() + 20, rectangle.getY() + 240);
                canvas.addImage(imageData, new Rectangle((int) qrPosition.getX(), (int) qrPosition.getY(), qrCodeSize, qrCodeSize), true);

                // Show header
                Canvas managedCanvas = new Canvas(canvas, canvas.getDocument(), rectangle);
                Paragraph paragraph = generateRapidPassHeader(rapidPass, rectangle);
                managedCanvas.add(paragraph);

//                managedCanvas.add(generateTitle(rapidPass, rectangle));

                Paragraph[] details = generateDetails(rapidPass, rectangle);

//                managedCanvas.add(details[0]);
                managedCanvas.add(details[1]);

//
                IBlockElement[] iBlockElements = generateAporCode(rapidPass, rectangle);

                float aporWidth = rectangle.getWidth() * 2f - 80;
                float aporHeight = rectangle.getHeight() * 0.15f;
                float aporMargin = 40;

                canvas.setFillColor(ColorConstants.BLACK);
                canvas.rectangle(new Rectangle(rectangle.getX() + aporMargin, rectangle.getY() + aporMargin, aporWidth, aporHeight));
                canvas.fillStroke();

                managedCanvas.add(iBlockElements[0]);
                managedCanvas.add(iBlockElements[1]);

                Paragraph[] paragraphs = generateValidUntil(rapidPass, rectangle);
                managedCanvas.add(paragraphs[0]);
                managedCanvas.add(paragraphs[1]);
                managedCanvas.add(paragraphs[2]);
                managedCanvas.add(paragraphs[3]);

                Paragraph personsName = generatePersonsName(rapidPass, rectangle);
                managedCanvas.add(personsName);

                canvas.concatMatrix(inverse);

            } catch (NoninvertibleTransformException | ParseException e) {
                e.printStackTrace();
            }
//            catch (ParseException e) {
//                e.printStackTrace();
//            }

        };

//        Rectangle leftCopy = new Rectangle(0, 0, A4_PAGE_WIDTH / 2, A4_PAGE_HEIGHT / 2);
        Rectangle rightCopy = new Rectangle(A4_PAGE_WIDTH / 2, 0, A4_PAGE_WIDTH / 2, A4_PAGE_HEIGHT / 2);

//        renderOnCanvas(firstPage, leftCopy, generatePdf);
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