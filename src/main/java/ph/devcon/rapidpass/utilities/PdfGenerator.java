package ph.devcon.rapidpass.utilities;

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

import ph.devcon.rapidpass.api.models.ApprovedRapidPass;

public class PdfGenerator {

    private final String filepath;
    //should be changed if logo will be altered.
    private final String pathToDctxLogo = "src/main/resources/light-bg.png";
    
    public PdfGenerator(String filepath) {
        this.filepath = filepath;
    }

    // creates the pdf document and set its properties
    private Document createDocument() throws FileNotFoundException {

        PdfDocument pdfdocument = new PdfDocument(new PdfWriter(filepath));

        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
        document.setMargins(-50,-50,-50,-50);

        return document;
    }

    //prepares the image
    private ImageData prepareImage(String imagePath) throws MalformedURLException {
        return ImageDataFactory.create(imagePath);
    }

    public int generateVehiclePdf(String _qrcodePath, ApprovedRapidPass approvedRapidPass ) {

        Document document;
        try {
            document = createDocument();
        } catch (FileNotFoundException e){
            return 0;
        }

        //qrcode image
        Image qrcode;
        Image dctxLogo;
        try {
            qrcode = new Image(prepareImage(_qrcodePath));
            dctxLogo = new Image(prepareImage(pathToDctxLogo));
        } catch (Exception e) {
            return 0;
        }
        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400,0);

        //processes the data that will be on the pdf

        //properties for control number
        Paragraph vehicleId = new Paragraph();
        vehicleId.setFontSize(44);
        vehicleId.setTextAlignment(TextAlignment.CENTER);
        vehicleId.setMarginTop(-50);
        vehicleId.setBold();
        vehicleId.add(approvedRapidPass.getControlNumber() +"\n");

        //settings for person details
        Paragraph vehicleDetails = new Paragraph();
        vehicleDetails.setFontSize(20);
        vehicleDetails.setMarginLeft(70);
        vehicleDetails.add("Access Type:\t" + approvedRapidPass.getAccessType() + "\n" +
                "Pass Type:\t" + approvedRapidPass.getPassType() + "\n" +
                "Plate:\t" + approvedRapidPass.getPlateNum() + "\n" +
                "Name:\t" + approvedRapidPass.getName());

        //writes to the document
        document.add(qrcode);
        document.add(vehicleId);
        document.add(vehicleDetails);
        document.add(dctxLogo);
                
        document.close();
        
        return 1;

    }

    public int generateIndividualPdf(String _qrcodePath, ApprovedRapidPass approvedRapidPass)  {

        Document document;

        //creates and sets the pdf document
        try {
            document = createDocument();
        } catch (FileNotFoundException e){
            return 0;
        }

        //qrcode image
        Image qrcode;
        Image dctxLogo;
        try {
            qrcode = new Image(prepareImage(_qrcodePath));
            dctxLogo = new Image(prepareImage(pathToDctxLogo));
        } catch (Exception e) {
            return 0;
        }

        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400,0);

        //settings for ID
        Paragraph individualId = new Paragraph();
        individualId.setFontSize(44);
        individualId.setTextAlignment(TextAlignment.CENTER);
        individualId.setBold();
        individualId.add(approvedRapidPass.getControlNumber() + "\n");

        //settings for person details
        //contains the lines that will be on the pdf
        Paragraph individualDetails = new Paragraph();
        individualDetails.setFontSize(20);
        individualDetails.setMarginLeft(20);
        individualDetails.add("Name:\t" + approvedRapidPass.getName() + "\n" +
                "Access Type:\t" + approvedRapidPass.getAccessType() + "\n" +
                "Pass Type:\t" + approvedRapidPass.getPassType() + "\n" +
                "Company:\t" + approvedRapidPass.getCompany());

        document.add(qrcode);
        document.add(individualId);
        document.add(individualDetails);
        document.add(dctxLogo);
                        
        document.close();
        
        return 1;
    }    
}