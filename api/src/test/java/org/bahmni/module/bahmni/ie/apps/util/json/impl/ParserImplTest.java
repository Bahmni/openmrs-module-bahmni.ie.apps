package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import com.itextpdf.text.DocumentException;
import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
public class ParserImplTest {
    @Mock
    private BahmniPDFFormImpl bahmniPDFForm;

    private ParserImpl parserImpl;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("bahmni.pdf.directory", "target/test/temp/");
    }

    @AfterClass
    public static void afterClass() throws IOException {
        FileUtils.deleteDirectory(new File("target/test/temp/"));
    }

    @Test
    public void shouldParseJsonAndGetPdfTitle() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true},\"resources\":[{\"value\":{\"name\":\"HackathonForm\",\"defaultLocale\":\"en\",\"controls\":[{\"translationKey\":\"LABEL_1\",\"type\":\"label\",\"value\":\"Label-1\",\"id\":\"1\"}]}}]}");;

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).setTitle("HackathonForm");
    }

    @Test
    public void shouldParseJsonAndPrintLabelControl() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true},\"resources\":[{\"value\":{\"name\":\"HackathonForm\",\"defaultLocale\":\"en\",\"controls\":[{\"translationKey\":\"LABEL_1\",\"type\":\"label\",\"value\":\"Label-1\",\"id\":\"1\"}]}}]}");;

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addLabel("Label-1");
    }
}
