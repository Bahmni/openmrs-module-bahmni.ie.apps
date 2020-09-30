package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(PowerMockRunner.class)
public class BahmniPDFFormImplTest {
    String title = "Bahmni Form";
    BahmniPDFFormImpl bahmniPDFForm;

    @Before
    public void setUp() throws FileNotFoundException, DocumentException {
        bahmniPDFForm = new BahmniPDFFormImpl(title);
    }

    @Test
    public void shouldCreatePdfFile() throws IOException {
        String filename = bahmniPDFForm.create();

        assertThat(new File(filename).exists(), is(true));
    }

    @Test
    public void shouldAddTextField() throws IOException {
        bahmniPDFForm.addTextField("random");
        bahmniPDFForm.create();
        PdfReader reader = new PdfReader("BahmniForm.pdf");

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("random"));
    }

    @Test
    public void shouldAddNumericField() throws IOException {
        bahmniPDFForm.addNumericField("Weight", "kg");
        bahmniPDFForm.create();
        PdfReader reader = new PdfReader("BahmniForm.pdf");

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("Weight"));
        assertThat(textFromPage, containsString("kg"));
    }

    @Test
    public void shouldAddSection() throws IOException {
        bahmniPDFForm.beginSection("MySection");
        bahmniPDFForm.addTextField("textField");
        bahmniPDFForm.endSection();
        bahmniPDFForm.create();
        PdfReader reader = new PdfReader("BahmniForm.pdf");

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("MySection"));
    }

    @Test
    public void shouldAddDateTimeField() throws IOException {
        bahmniPDFForm.addDateTimeField("DateTime");
        bahmniPDFForm.create();
        PdfReader reader = new PdfReader("BahmniForm.pdf");

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("DateTime"));
        assertThat(textFromPage, containsString("AM/PM"));
    }
}
