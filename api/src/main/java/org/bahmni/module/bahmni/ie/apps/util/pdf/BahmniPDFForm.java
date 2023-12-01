package org.bahmni.module.bahmni.ie.apps.util.pdf;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.List;

public interface BahmniPDFForm {

    String create() throws IOException, DocumentException;

    void setTitle(String title);

    void addLabel(String labelText);

    void addTextField(String textFieldLabel) throws IOException, DocumentException;

    void addNumericField(String numericFieldLabel, String unit);

    void beginSection(String sectionTitle);

    void endSection();

    void addDateField(String dateFieldLabel);

    void addDateTimeField(String dateTimeFieldLabel);

    void addBooleanField(String booleanFieldLabel);

    void addCodedField(String codedFieldLabel, List<String> codes);
}
