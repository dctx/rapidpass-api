package ph.devcon.rapidpass.utilities;

import java.io.File;

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


//
//  Service for generating Pdf using File qrcode file from QRCode Generator
//
public class PdfGenerator {

    private final String filepath;
    //should be changed if logo will be altered.
    private final String pathToDctxLogo = "src/main/resources/light-bg.png";
    
    public PdfGenerator(String filepath) {
        this.filepath = filepath;
    }

    // creates the pdf document and set its properties
    private Document createDocument() throws Exception {

        PdfDocument pdfdocument = new PdfDocument(new PdfWriter(filepath));

        pdfdocument.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfdocument);
        document.setMargins(-50,-50,-50,-50);

        return document;
    }

    //prepares the image
    private ImageData prepareImage(String imagePath) throws Exception {
        return ImageDataFactory.create(imagePath);
    }

    public PdfDocument generatePdf(File qrcodeFile, ApprovedRapidPass approvedRapidPass) {
        
        Document document = null;

        try {
            document = createDocument();
        } catch (Exception e){
            e.printStackTrace();
        }

        //qrcode image
        Image qrcode = null;
        Image dctxLogo = null;
        try {
            qrcode = new Image(prepareImage(qrcodeFile.getAbsolutePath()));
            dctxLogo = new Image(prepareImage(pathToDctxLogo));

            dctxLogo.scaleToFit(200, 100);
            dctxLogo.setFixedPosition(400,0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //processes the data that will be on the pdf

        //properties for control number
        Paragraph controlNumbeParagraph = new Paragraph();
        controlNumbeParagraph.setFontSize(44);
        controlNumbeParagraph.setTextAlignment(TextAlignment.CENTER);
        controlNumbeParagraph.setMarginTop(-50);
        controlNumbeParagraph.setBold();
        controlNumbeParagraph.add(approvedRapidPass.getControlNumber() +"\n");

        Paragraph details = new Paragraph();

        // checks if pass type is individual or vehicle
        if(approvedRapidPass.getPassType().toLowerCase().equals("individual")) {
            details.setFontSize(20);
            details.setMarginLeft(70);
            details.add("Name:\t" + approvedRapidPass.getName() + "\n" +
                    "Access Type:\t" + approvedRapidPass.getAccessType() + "\n" +
                    "Pass Type:\t" + approvedRapidPass.getPassType() + "\n" +
                    "Company:\t" + approvedRapidPass.getCompany());
        } else if (approvedRapidPass.getPassType().toLowerCase().equals("vehicle")) {
            details.setFontSize(20);
            details.setMarginLeft(70);
            details.add("Access Type:\t" + approvedRapidPass.getAccessType() + "\n" +
                    "Pass Type:\t" + approvedRapidPass.getPassType() + "\n" +
                    "Plate:\t" + approvedRapidPass.getPlateNum() + "\n" +
                    "Name:\t" + approvedRapidPass.getName());
        }

        //writes to the document
        document.add(qrcode);
        document.add(controlNumbeParagraph);
        document.add(details);
        document.add(dctxLogo);
                
        document.close();

        return document.getPdfDocument();
    } 
}