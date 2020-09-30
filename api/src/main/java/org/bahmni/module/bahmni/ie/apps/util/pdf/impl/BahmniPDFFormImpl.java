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
    private final Document document;
    private final PdfWriter writer;
    private final String filename = "BahmniForm.pdf";
    private String html = "";

    public BahmniPDFFormImpl(String title) throws FileNotFoundException, DocumentException {
        this.title = title;
        document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        addTitle();
    }

    @Override
    public String create() throws IOException {
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new ByteArrayInputStream(html.getBytes()));
        document.close();
        return filename;
    }

    private void addTitle() throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk(title, font);
        document.add(chunk);
    }

    @Override
    public void addTextField(String textFieldLabel) {
        html += "<table><tr><td style=\"width: 30%;\">" + textFieldLabel + "</td><td style=\"width: 60%; border: 3px solid black; height: 50px;\"></td></tr></table>";
    }
}
