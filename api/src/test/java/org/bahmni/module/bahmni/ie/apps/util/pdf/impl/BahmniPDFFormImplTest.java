package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class BahmniPDFFormImplTest {
    BahmniPDFFormImpl bahmniPDFForm;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("bahmni.pdf.directory", "target/test/temp/");
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.deleteDirectory(new File("target/test/temp/"));
    }

    @Before
    public void setUp() throws IOException, DocumentException {
        bahmniPDFForm = new BahmniPDFFormImpl();
    }

    @Test
    public void shouldCreatePdfFileWithTitle() throws IOException {
        bahmniPDFForm.setTitle("MyTitle");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(new File(filename).exists(), is(true));
        assertThat(textFromPage, containsString("MyTitle"));
    }

    @Test
    public void shouldAddTextField() throws IOException {
        bahmniPDFForm.addTextField("random");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("random"));
    }

    @Test
    public void shouldAddLabel() throws IOException {
        bahmniPDFForm.addLabel("randomLabel");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("randomLabel"));
    }

    @Test
    public void shouldAddNumericField() throws IOException {
        bahmniPDFForm.addNumericField("Weight", "kg");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("Weight"));
        assertThat(textFromPage, containsString("kg"));
    }

    @Test
    public void shouldAddSection() throws IOException {
        bahmniPDFForm.beginSection("MySection");
        bahmniPDFForm.addTextField("textField");
        bahmniPDFForm.endSection();
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("MySection"));
    }

    @Test
    public void shouldAddDateTimeField() throws IOException {
        bahmniPDFForm.addDateTimeField("DateTime");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("DateTime"));
        assertThat(textFromPage, containsString("AM/PM"));
    }

    @Test
    public void shouldAddDateField() throws IOException {
        bahmniPDFForm.addDateField("Date");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("Date"));
    }

    @Test
    public void shouldAddBooleanField() throws IOException {
        bahmniPDFForm.addBooleanField("Boolean");
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("Yes"));
        assertThat(textFromPage, containsString("No"));
    }

    @Test
    public void shouldAddCodedField() throws IOException {
        bahmniPDFForm.addCodedField("Coded", Arrays.asList("code1", "code2", "code3", "code4", "code5"));
        String filename = bahmniPDFForm.create();
        PdfReader reader = new PdfReader(filename);

        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("code1"));
        assertThat(textFromPage, containsString("code2"));
        assertThat(textFromPage, containsString("code3"));
        assertThat(textFromPage, containsString("code4"));
        assertThat(textFromPage, containsString("code5"));
    }
}
