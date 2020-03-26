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

import ph.devcon.rapidpass.api.models.ApprovedRapidPass;

public class PdfGenerator {

    private final String filepath;
    
    public PdfGenerator(String filepath) {
        this.filepath = filepath;
    }

    public int generateVehiclePdf(String _qrcodePath, ApprovedRapidPass approvedRapidPass ) {

        PdfDocument pdfdocument;
        ImageData data;
        ImageData _dctxLogo;

        try {
            pdfdocument = new PdfDocument(new PdfWriter(filepath));
            data = ImageDataFactory.create(_qrcodePath);
            _dctxLogo = ImageDataFactory.create("src/main/resources/light-bg.png");
        } catch (Exception e) {
            return 0;
        }

        //creates and sets the pdf document
        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
        document.setMargins(-50,-50,-50,-50);

        //process the image
        Image qrcode = new Image(data);

        //dctxLogo
        Image dctxLogo = new Image(_dctxLogo);
        dctxLogo.scaleToFit(200, 100);
        dctxLogo.setFixedPosition(400,0);
        //settings for ID
        Paragraph vehicleId = new Paragraph();
        vehicleId.setFontSize(44);
        vehicleId.setTextAlignment(TextAlignment.CENTER);
        vehicleId.setMarginTop(-50);
        vehicleId.setBold();
        vehicleId.add(approvedRapidPass.getControlNumber() +"\n");

        //settings for person details
        //contains the lines that will be on the pdf
        Paragraph vehicleDetails = new Paragraph();
        vehicleDetails.setFontSize(20);
        vehicleDetails.setMarginLeft(70);
        vehicleDetails.add("Access Type:\t" + approvedRapidPass.getAccessType() + "\n" +
                "Pass Type:\t" + approvedRapidPass.getPassType() + "\n" +
                "Plate:\t" + approvedRapidPass.getPlateNum() + "\n" +
                "Name:\t" + approvedRapidPass.getName());

        document.add(qrcode);
        document.add(vehicleId);
        document.add(vehicleDetails);
        document.add(dctxLogo);
                
        document.close();
        
        return 1;

    }

    public int generateIndividualPdf(String _qrcodePath, ApprovedRapidPass approvedRapidPass)  {

        PdfDocument pdfdocument;
        ImageData data;
        ImageData _dctxLogo;

        try {
            pdfdocument = new PdfDocument(new PdfWriter(filepath));
            data = ImageDataFactory.create(_qrcodePath);
            _dctxLogo = ImageDataFactory.create("src/main/resources/light-bg.png");
        } catch (Exception e) {
            return 0;
        }

        //creates and sets the pdf document
        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
        document.setMargins(-50,-50,-50,-50);

        //process the image
        Image qrcode = new Image(data);

        //dctxLogo
        Image dctxLogo = new Image(_dctxLogo);
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