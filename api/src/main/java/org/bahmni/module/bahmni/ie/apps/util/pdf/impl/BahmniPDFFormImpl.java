package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BahmniPDFFormImpl implements BahmniPDFForm {
    private final String title;
    private Document document;

    public BahmniPDFFormImpl(String title) {
        this.title = title;
        document = new Document();
    }

    @Override
    public String create() throws FileNotFoundException, DocumentException {
        String filename = "BahmniForm.pdf";
        PdfWriter.getInstance(document, new FileOutputStream(filename));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk(title, font);

        document.add(chunk);
        document.close();
        return filename;
    }

    @Override
    public void addTextField(String textFieldLabel) throws IOException, DocumentException {
        String filename = "BahmniForm.pdf";
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        String html = "<table><tr><td style=\"width: 30%;\">" + textFieldLabel + "</td><td style=\"width: 60%; border: 3px solid black; height: 50px;\"></td></tr></table>";
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new ByteArrayInputStream(html.getBytes()));
        document.close();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        BahmniPDFForm bahmniPDFForm = new BahmniPDFFormImpl("title");
        bahmniPDFForm.addTextField("Hello");
    }
}
