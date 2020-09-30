package org.bahmni.module.bahmni.ie.apps.util.pdf;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface BahmniPDFForm {

    String create() throws IOException, DocumentException;

    void addTextField(String textFieldLabel) throws IOException, DocumentException;

    void addNumericField(String numericFieldLabel, String unit);

    void beginSection(String sectionTitle);

    void endSection();

    void addDateTimeField(String dateTimeFieldLabel);
}
