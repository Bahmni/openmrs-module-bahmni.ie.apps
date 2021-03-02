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
import java.util.List;
import java.util.UUID;

public class BahmniPDFFormImpl implements BahmniPDFForm {
    private static final String DEFAULT_PDF_FOLDER_PATH = "/home/bahmni/pdf/";
    private static final String BAHMNI_FORM_PATH_PDF = "bahmni.pdf.directory";

    private String title;
    private final Document document;
    private final PdfWriter writer;
    private final String filename;
    private String html = "";

    public BahmniPDFFormImpl() throws IOException, DocumentException {
        filename = createDirsAndGetFilePath();
        document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
    }

    @Override
    public String create() throws IOException {
        addTitle();
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
        if (title != null) {
            html = "<center><h2>" + title + "</h2></center>" + html;
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addLabel(String labelText) {
        html += "<p>" + labelText + "</p>";
        html += addLineBreak();
    }

    @Override
    public void addTextField(String textFieldLabel) {
        html += "<table style=\"width: 100%; max-width: 100%;\"><tr><td style=\"width: 35%;\">" + textFieldLabel + "</td><td style=\" width:5% \"></td><td style=\"width: 60%; border-bottom: 1px ridge black;\"></td></tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addNumericField(String numericFieldLabel, String unit) {
        html += "<table><tr><td style=\"width: 35%;\">" + numericFieldLabel + "</td><td style=\"width:5%\"></td><td style=\"border-bottom : 1px ridge black;width:40%\"></td><td>" + unit + "</td></tr></table>";
        html += addLineBreak();
    }

    @Override
    public void beginSection(String sectionTitle) {
        html += "<h4>" + sectionTitle + "</h4>";
        html += "<table style=\"width: 100%; border: 1px solid black;\"><tr><td>";
        html += addLineBreak();
    }

    @Override
    public void endSection() {
        html += "</td></tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addDateTimeField(String dateTimeFieldLabel) {
        String dateTimeblank = "__/___/____ , __:__";
        html += "<table><tr><td style=\"width: 35%;\">" + dateTimeFieldLabel +" (dd/mm/yyyy)" +"</td><td style=\"width: 5%;\"></td><td>" + dateTimeblank + "</td><td style=\"width: 30%;\">" + "AM/PM" + "</td></tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addBooleanField(String booleanFieldLabel) {
        String checkBoxStyle = "\"float: left;height: 20px;width: 20px;margin-bottom: 15px;border: 1px solid black;clear: both;\"";
        html += "<table><tr><td style=\"width: 30%;\">" + booleanFieldLabel + "</td><td style=" + checkBoxStyle + "> </td> <td>Yes</td> <td style=" + checkBoxStyle + "</td> <td>No</td> </tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addCodedField(String codedFieldLabel, List<String> codes) {
        html += "<table style=\"width: 100%; max-width: 100%;\"><tr><td style=\"width: 35%;\">" + codedFieldLabel + "</td>"+"<td style=\"width: 10%;\"></td>" + generateDynamicCode(codes) + "</tr></table>";
        html += addLineBreak();
    }

    private String generateDynamicCode(List<String> codes) {
        String checkBoxStyle = "\"float: left;height: 20px;width: 20px;margin-bottom: 15px;border: 1px solid black;clear: both;\"";
        StringBuilder codeHtml = new StringBuilder();
        for (int index = 0; index < codes.size(); index++) {
            if (index % 2 == 0) {
                if (index != 0) {
                    codeHtml.append("<td></td>");
                }
                codeHtml.append("<td style=").append(checkBoxStyle).append("> </td> <td>").append(codes.get(index)).append("</td>");
            } else {
                codeHtml.append("<td style=").append(checkBoxStyle).append("> </td> <td>").append(codes.get(index)).append("</td>");
                codeHtml.append("</tr><tr>");
            }
        }
        return codeHtml.toString();
    }

    private String addLineBreak() {
        return "<br/>";
    }
}