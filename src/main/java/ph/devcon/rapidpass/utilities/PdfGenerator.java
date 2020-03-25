package ph.devcon.rapidpass.utilities;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;


public class PdfGenerator {

    private final String filepath = "resources/hello.pdf";
    private ImageData _dctxLogo;
    private PdfDocument pdfdocument;
    private Document document;
    private ImageData data;
    private Image qrcode;
    private Image dctxLogo;

    private int preparePdf(String _qrcodePath) {
        try {
            pdfdocument = new PdfDocument(new PdfWriter(filepath));
            data = ImageDataFactory.create(_qrcodePath);
            _dctxLogo = ImageDataFactory.create("resources/light-bg.png");
        } catch (Exception e) {
            System.out.println(e);
            return 0;
        }

        //creates and sets the pdf document
        pdfdocument.setDefaultPageSize(PageSize.A4);
        document = new Document(pdfdocument);
        document.setMargins(-50,-50,-50,-50);

        //process the image
        qrcode = new Image(data);

        //dctxLogo
        dctxLogo = new Image(_dctxLogo);
        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400,0);

        return 1;
    }

    public void writeToPdf(Paragraph id, Paragraph details) {

        document.add(qrcode);
        document.add(id);
        document.add(details);
        document.add(dctxLogo);

        document.close();
    }

    public void generateVehiclePdf(String _qrcodePath)  {

        preparePdf(_qrcodePath);

        //settings for ID
        Paragraph vehicleId = new Paragraph();
        vehicleId.setFontSize(44);
        vehicleId.setTextAlignment(TextAlignment.CENTER);
        vehicleId.setMarginTop(-50);
        vehicleId.setBold();
        vehicleId.add("CONTROL NUMBER\n");

        //settings for person details
        Paragraph vehicleDetails = new Paragraph();
        vehicleDetails.setFontSize(20);
        vehicleDetails.setMarginLeft(70);
        vehicleDetails.add("Access Type:\txxx\n" +
                "Plate:\tPL4-T3X\n" +
                "Driver:\tJuan Dela Kruz\n" +
                "Origin:\tBalong Malalim");

        writeToPdf(vehicleId, vehicleDetails);

    }

    public void generateIndividualPdf(String _qrcodePath)  {

        preparePdf(_qrcodePath);

        //settings for ID
        Paragraph individualId = new Paragraph();
        individualId.setFontSize(44);
        individualId.setTextAlignment(TextAlignment.CENTER);
        individualId.setBold();
        individualId.add("CONTROL NUMBER\n");

        //settings for person details
        Paragraph vehicleDetails = new Paragraph();
        vehicleDetails.setFontSize(20);
        vehicleDetails.setMarginLeft(20);
        vehicleDetails.add("Name:\tJuan Dela Kruz\n" +
                "Access Type:\tX\n" +
                "Valid Until: xxx-xx");

        writeToPdf(individualId, vehicleDetails);

    }
    
}