package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.bahmni.module.bahmni.ie.apps.config.PdfFormConfig;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.ParserImpl;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFCreationListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class BahmniPDFFormImpl implements BahmniPDFForm {
    private static final String DEFAULT_PDF_FOLDER_PATH = "/home/bahmni/pdf/";
    private static final String BAHMNI_FORM_PATH_PDF = "bahmni.pdf.directory";
    private static final String PDF_PRODUCER = "Bahmni PDF Form (OpenPDF/Flying Saucer)";

    private String title;
    private final String filename;
    private String html = "";

    PdfFormConfig pdfFormConfig;
    private Logger logger = Logger.getLogger(ParserImpl.class);

    public BahmniPDFFormImpl() throws IOException {
        filename = createDirsAndGetFilePath();
        pdfFormConfig = new PdfFormConfig();
    }

    @Override
    public String create() throws IOException, DocumentException {
        addTitle();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setListener(new PDFCreationListener() {
            @Override
            public void preOpen(ITextRenderer renderer) {
            }

            @Override
            public void preWrite(ITextRenderer renderer, int pageCount) {
                PdfWriter writer = renderer.getWriter();
                if (writer != null) {
                    writer.getInfo().put(PdfName.PRODUCER, new PdfString(PDF_PRODUCER));
                }
            }

            @Override
            public void onClose(ITextRenderer renderer) {
            }
        });

        renderer.setDocumentFromString(wrapAsXhtml(html));
        renderer.layout();
        try (OutputStream outputStream = new FileOutputStream(filename)) {
            renderer.createPDF(outputStream);
        }
        return filename;
    }

    private String wrapAsXhtml(String bodyHtml) {
        return "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" +
                "<style>@page { size: A4; margin: 36pt; }</style></head><body>" + bodyHtml + "</body></html>";
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
            html = "<center><h2>" + escape(title) + "</h2></center>" + html;
        }
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addLabel(String labelText) {
        html += "<p>" + escape(labelText) + "</p>";
        html += addLineBreak();
    }

    @Override
    public void addTextField(String textFieldLabel) {
        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setMinimumWidth(60);
        pdfFormConfig.setWidth(60);
        pdfFormConfig.setContentFieldBox(1, "ridge", "black");
        String contentColumnStyles = pdfFormConfig.getStyles();


        String labelColumn = "<td " + labelColumnStyles + ">" + escape(textFieldLabel) + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + contentColumnStyles + "></td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + "</tr></table>";

        html += addLineBreak();

        logger.warn(html);
    }

    @Override
    public void addNumericField(String numericFieldLabel, String unit) {
        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(50);
        pdfFormConfig.setMinimumWidth(50);
        pdfFormConfig.setContentFieldBox(1, "ridge", "black");
        String contentColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setMinimumWidth(10);
        pdfFormConfig.setWidth(10);
        String unitColumnStyles = pdfFormConfig.getStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + escape(numericFieldLabel) + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + contentColumnStyles + "></td>";
        String unitColumn = "<td " + unitColumnStyles + ">" + escape(unit) + "</td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + unitColumn + "</tr></table>";
        html += addLineBreak();

        logger.warn(html);
    }

    @Override
    public void beginSection(String sectionTitle) {
        html += "<h4>" + escape(sectionTitle) + "</h4>";
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
        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(60);
        pdfFormConfig.setMinimumWidth(60);
        String dateTimeBlankColumnLStyles = pdfFormConfig.getStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + escape(dateTimeFieldLabel) +" (dd/mm/yyyy)"+ "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String dateTimeBlankColumn  = "<td "+dateTimeBlankColumnLStyles+">__/___/____ , __:__ AM/PM</td>";

        html += "<table  " +TableStyles+"><tr>"+labelColumn  + emptyColumn + dateTimeBlankColumn +  "</tr></table>";
        html += addLineBreak();
    }

    @Override
    public void addDateField(String dateFieldLabel) {
        pdfFormConfig.setMaximumWidth(100);
        pdfFormConfig.setWidth(100);
        String TableStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(35);
        String labelColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(5);
        String emptyColumnStyles = pdfFormConfig.getStyles();

        pdfFormConfig.setWidth(60);
        pdfFormConfig.setMinimumWidth(60);
        String dateBlankColumnLStyles = pdfFormConfig.getStyles();

        String labelColumn = "<td " + labelColumnStyles + ">" + escape(dateFieldLabel) +" (dd/mm/yyyy)"+ "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String dateBlankColumn  = "<td "+dateBlankColumnLStyles+">__/___/____ </td>";

        html += "<table  " +TableStyles+"><tr>"+labelColumn  + emptyColumn + dateBlankColumn +  "</tr></table>";
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

        String labelColumn = "<td " + labelColumnStyles + ">" + escape(booleanFieldLabel) + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";
        String contentColumn = "<td " + checkBoxStyle + "> </td> <td>Yes</td> <td " + checkBoxStyle + "> </td> <td>No</td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + contentColumn + "</tr></table>";

        html += addLineBreak();
    }

    @Override
    public void addCodedField(String codedFieldLabel, List<String> codes) {

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

        String labelColumn = "<td " + labelColumnStyles + ">" + escape(codedFieldLabel) + "</td>";
        String emptyColumn = "<td " + emptyColumnStyles + "></td>";

        html += "<table " + TableStyles + "><tr>" + labelColumn + emptyColumn + "</tr>";
        for (int index = 0; index < codes.size(); index++) {
            html += "<tr>" + emptyColumn + "<td " + checkBoxStyle + " ></td><td><label>" + escape(codes.get(index)) + "</label></td></tr>";
            html += "<tr>" + emptyColumn + "</tr>";
        }
        html += "</table>";
        html += addLineBreak();
    }

    private String addLineBreak() {
        return "<br/>";
    }

    private static String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
