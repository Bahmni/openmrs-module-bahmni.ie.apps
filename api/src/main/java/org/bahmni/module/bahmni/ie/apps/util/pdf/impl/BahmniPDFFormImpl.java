package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
}
