package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmni.ie.apps.config.PdfFormConfig;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.ParserImpl;
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

    PdfFormConfig pdfFormConfig;
    private Logger logger = Logger.getLogger(ParserImpl.class);

    public BahmniPDFFormImpl() throws IOException, DocumentException {
        filename = createDirsAndGetFilePath();
        document = new Document();
        pdfFormConfig = new PdfFormConfig();
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

        String TableStyles = pdfFormConfig.getTableStyles();

        String labelColumnStyles = pdfFormConfig.getLabelColumnStyles();

        String emptyColumnStyles = pdfFormConfig.getEmptyColumnStyles();

        pdfFormConfig.setMinimumWidth(60);
        pdfFormConfig.setWidth(60);
        pdfFormConfig.setContentFieldBox(1, "ridge", "black");
        String contentColumnStyles = pdfFormConfig.getStyles();


        String labelColumn = "<td " + labelColumnStyles + ">" + textFieldLabel + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + contentColumnStyles + "></td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + "</tr></table>";

        html += addLineBreak();

        logger.warn(html);
    }

    @Override
    public void addNumericField(String numericFieldLabel, String unit) {

        String TableStyles = pdfFormConfig.getTableStyles();

        String labelColumnStyles = pdfFormConfig.getLabelColumnStyles();

        String emptyColumnStyles = pdfFormConfig.getEmptyColumnStyles();

        pdfFormConfig.setWidth(50);
        pdfFormConfig.setMinimumWidth(50);
        pdfFormConfig.setContentFieldBox(1, "ridge", "black");
        String contentColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setMinimumWidth(10);
        pdfFormConfig.setWidth(10);
        String unitColumnStyles = pdfFormConfig.getStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + numericFieldLabel + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + contentColumnStyles + "></td>";
        String unitColumn = "<td " + unitColumnStyles + ">" + unit + "</td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + unitColumn + "</tr></table>";
        html += addLineBreak();

        logger.warn(html);
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
        String TableStyles = pdfFormConfig.getTableStyles();

        String labelColumnStyles = pdfFormConfig.getLabelColumnStyles();

        String emptyColumnStyles = pdfFormConfig.getEmptyColumnStyles();

        pdfFormConfig.setWidth(60);
        pdfFormConfig.setMinimumWidth(60);
        String dateTimeBlankColumnLStyles = pdfFormConfig.getStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + dateTimeFieldLabel +" (dd/mm/yyyy)"+ "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String dateTimeBlankColumn  = "<td "+dateTimeBlankColumnLStyles+">__/___/____ , __:__ AM/PM</td>";

        html += "<table  " +TableStyles+"><tr>"+labelColumn  + emptyColumn + dateTimeBlankColumn +  "</tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addBooleanField(String booleanFieldLabel) {

        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(60);
        pdfFormConfig.setCheckBox(20, 20, 50, 1);
        String checkBoxStyle = pdfFormConfig.getStyles();


        String labelColumn = "<td " + labelColumnStyles + ">" + booleanFieldLabel + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + checkBoxStyle + "> </td> <td>Yes</td> <td " + checkBoxStyle + "> </td> <td>No</td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + "</tr></table>";

        html += addLineBreak();
    }

    @Override
    public void addCodedField(String codedFieldLabel, List<String> codes) {
        String TableStyles = pdfFormConfig.getTableStyles();


        String labelColumnStyles = pdfFormConfig.getLabelColumnStyles();

        String emptyColumnStyles = pdfFormConfig.getEmptyColumnStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + codedFieldLabel + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + generateDynamicCode(codes) + "</tr></table>";

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
