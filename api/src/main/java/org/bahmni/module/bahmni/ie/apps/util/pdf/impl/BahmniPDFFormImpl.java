package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class BahmniPDFFormImpl implements BahmniPDFForm {
    private final String DEFAULT_PDF_FOLDER_PATH = "/home/bahmni/pdf/";
    private final String BAHMNI_FORM_PATH_PDF = "bahmni.pdf.directory";

    private final String title;
    private final Document document;
    private final PdfWriter writer;
    private final String filename;
    private String html = "";

    public BahmniPDFFormImpl(String title) throws IOException, DocumentException {
        this.title = title;
        filename = createDirsAndGetFilePath();
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

    private String createDirsAndGetFilePath() throws IOException {
        String pathPrefix = System.getProperty(BAHMNI_FORM_PATH_PDF, DEFAULT_PDF_FOLDER_PATH);
        String filename = UUID.randomUUID().toString();
        String pathSuffix = ".pdf";
        String fullPath = pathPrefix + filename + pathSuffix;

        Files.createDirectories(Paths.get(pathPrefix));

        return fullPath;
    }

    private void addTitle() {
        html += "<center><h2>" + title + "</h2></center>";
    }

    @Override
    public void addLabel(String labelText) {
        html += "<p>" + labelText + "</p>";
    }

    @Override
    public void addTextField(String textFieldLabel) {
        html += "<table style=\"width: 100%; max-width: 100%;\"><tr><td style=\"width: 30%;\">" + textFieldLabel + "</td><td style=\"width: 70%; border: 3px solid black; height: 50px;\"></td></tr></table>";
    }

    @Override
    public void addNumericField(String numericFieldLabel, String unit) {
        String blank = "______________";
        html += "<table><tr><td style=\"width: 30%;\">" + numericFieldLabel + "</td><td>" + blank + "</td><td style=\"width: 30%;\">" + unit +"</td></tr></table>";
    }

    @Override
    public void beginSection(String sectionTitle) {
        html += "<h4>" + sectionTitle + "</h4>";
        html += "<table style=\"width: 100%; border: 1px solid black;\"><tr><td>";
    }

    @Override
    public void endSection() {
        html += "</td></tr></table>";
    }

    @Override
    public void addDateTimeField(String dateTimeFieldLabel) {
        String dateTimeblank = "__/___/____ , __:__";
        html += "<table><tr><td style=\"width: 30%;\">" + dateTimeFieldLabel + "</td><td>" + dateTimeblank + "</td><td style=\"width: 30%;\">" + "AM/PM" +"</td></tr></table>";
    }
}
