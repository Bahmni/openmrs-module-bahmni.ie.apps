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

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeText() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true},\"resources\":[{\"value\":{\"name\":\"HackathonForm\",\"defaultLocale\":\"en\",\"controls\":[{\"type\":\"obsControl\",\"label\":{\"translationKey\":\"HACKATHON-SETMEMBER1-TEXT_2\",\"id\":\"2\",\"units\":\"\",\"type\":\"label\",\"value\":\"Hackathon-SetMember1-Text\"},\"properties\":{\"mandatory\":false,\"notes\":false,\"addMore\":false,\"hideLabel\":false},\"id\":\"2\",\"concept\":{\"name\":\"Hackathon-SetMember1-Text\",\"datatype\":\"Text\",\"conceptClass\":\"Misc\"},\"units\":null,\"hiNormal\":null,\"lowNormal\":null,\"hiAbsolute\":null,\"lowAbsolute\":null}]}}]}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addTextField("Hackathon-SetMember1-Text");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeNumeric() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true},\"resources\":[{\"value\":{\"name\":\"HackathonForm\",\"defaultLocale\":\"en\",\"controls\":[{\"type\":\"obsControl\",\"label\":{\"translationKey\":\"HACKATHON-SETMEMBER2-NUMERIC_3\",\"id\":\"3\",\"units\":\"(unit)\",\"type\":\"label\",\"value\":\"Hackathon-SetMember2-Numeric\"},\"properties\":{\"mandatory\":true,\"notes\":false,\"addMore\":false,\"hideLabel\":false},\"id\":\"3\",\"concept\":{\"name\":\"Hackathon-SetMember2-Numeric\",\"uuid\":\"e667b589-c1e2-4cfe-b1ca-d390aff69d12\",\"datatype\":\"Numeric\",\"conceptClass\":\"Misc\"},\"units\":\"unit\",\"hiNormal\":9,\"lowNormal\":5,\"hiAbsolute\":10,\"lowAbsolute\":1}]}}]}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addNumericField("Hackathon-SetMember2-Numeric","(unit)");
    }
}
