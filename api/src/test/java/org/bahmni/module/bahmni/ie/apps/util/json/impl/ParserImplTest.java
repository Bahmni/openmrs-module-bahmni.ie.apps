package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import com.itextpdf.text.DocumentException;
import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"controls\\\":[{\\\"translationKey\\\":\\\"LABEL_1\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Label-1\\\",\\\"id\\\":\\\"1\\\"}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).setTitle("HackathonForm");
    }

    @Test
    public void shouldParseJsonAndPrintLabelControl() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"controls\\\":[{\\\"translationKey\\\":\\\"LABEL_1\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Label-1\\\",\\\"id\\\":\\\"1\\\"}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addLabel("Label-1");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeText() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-SETMEMBER1-TEXT_2\\\",\\\"id\\\":\\\"2\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-SetMember1-Text\\\"},\\\"properties\\\":{\\\"mandatory\\\":false,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false},\\\"id\\\":\\\"2\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-SetMember1-Text\\\",\\\"datatype\\\":\\\"Text\\\",\\\"conceptClass\\\":\\\"Misc\\\"},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addTextField("Hackathon-SetMember1-Text");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeNumeric() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-SETMEMBER2-NUMERIC_3\\\",\\\"id\\\":\\\"3\\\",\\\"units\\\":\\\"(unit)\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-SetMember2-Numeric\\\"},\\\"properties\\\":{\\\"mandatory\\\":true,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false},\\\"id\\\":\\\"3\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-SetMember2-Numeric\\\",\\\"uuid\\\":\\\"e667b589-c1e2-4cfe-b1ca-d390aff69d12\\\",\\\"datatype\\\":\\\"Numeric\\\",\\\"conceptClass\\\":\\\"Misc\\\"},\\\"units\\\":\\\"unit\\\",\\\"hiNormal\\\":9,\\\"lowNormal\\\":5,\\\"hiAbsolute\\\":10,\\\"lowAbsolute\\\":1}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addNumericField("Hackathon-SetMember2-Numeric","(unit)");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeDatetime() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-SETMEMBER5-DATETIME_5\\\",\\\"id\\\":\\\"5\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-SetMember5-DateTime\\\"},\\\"properties\\\":{\\\"mandatory\\\":false,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false},\\\"id\\\":\\\"5\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-SetMember5-DateTime\\\",\\\"datatype\\\":\\\"Datetime\\\",\\\"conceptClass\\\":\\\"Misc\\\"},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}\"}]}}");
        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addDateTimeField("Hackathon-SetMember5-DateTime");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeBoolean() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"options\\\":[{\\\"name\\\":\\\"Yes\\\",\\\"translationKey\\\":\\\"BOOLEAN_YES\\\",\\\"value\\\":true},{\\\"name\\\":\\\"No\\\",\\\"translationKey\\\":\\\"BOOLEAN_NO\\\",\\\"value\\\":false}],\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-SETMEMBER6-BOOLEAN_6\\\",\\\"id\\\":\\\"6\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-SetMember6-Boolean\\\"},\\\"properties\\\":{\\\"mandatory\\\":true,\\\"notes\\\":true,\\\"addMore\\\":false,\\\"hideLabel\\\":false},\\\"id\\\":\\\"6\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-SetMember6-Boolean\\\",\\\"datatype\\\":\\\"Boolean\\\",\\\"conceptClass\\\":\\\"Misc\\\"},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        verify(bahmniPDFForm).addBooleanField("Hackathon-SetMember6-Boolean");
    }

    @Test
    public void shouldParseJsonAndPrintObsControlOfTypeCoded() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-CODED_PARENT_7\\\",\\\"id\\\":\\\"7\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-Coded_Parent\\\"},\\\"properties\\\":{\\\"mandatory\\\":false,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false,\\\"multiSelect\\\":true},\\\"id\\\":\\\"7\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-Coded_Parent\\\",\\\"datatype\\\":\\\"Coded\\\",\\\"conceptClass\\\":\\\"Misc\\\",\\\"answers\\\":[{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_1\\\",\\\"name\\\":\\\"Hackathon-Coded_1\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"displayString\\\":\\\"Hackathon-Coded_1\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_1_7\\\"},{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_2\\\",\\\"name\\\":\\\"Hackathon-Coded_2\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"names\\\":[{\\\"display\\\":\\\"Hackathon-Coded_2\\\",\\\"name\\\":\\\"Hackathon-Coded_2\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"}],\\\"displayString\\\":\\\"Hackathon-Coded_2\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_2_7\\\"}],\\\"properties\\\":{\\\"allowDecimal\\\":null}},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}\"}]}}");
        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);

        List<String> codes = new ArrayList<String>();
        codes.add("Hackathon-Coded_1");
        codes.add("Hackathon-Coded_2");
        verify(bahmniPDFForm).addCodedField("Hackathon-Coded_Parent", codes);
    }

    @Test
    public void shouldParseJsonAndPrintSection() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"section\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"SECTION_8\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Section-1\\\",\\\"id\\\":\\\"8\\\"},\\\"id\\\":\\\"8\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-CODED_PARENT_9\\\",\\\"id\\\":\\\"9\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-Coded_Parent\\\"},\\\"properties\\\":{\\\"mandatory\\\":false,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false,\\\"autoComplete\\\":false,\\\"multiSelect\\\":false,\\\"dropDown\\\":false},\\\"id\\\":\\\"9\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-Coded_Parent\\\",\\\"datatype\\\":\\\"Coded\\\",\\\"conceptClass\\\":\\\"Misc\\\",\\\"answers\\\":[{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_1\\\",\\\"name\\\":\\\"Hackathon-Coded_1\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"displayString\\\":\\\"Hackathon-Coded_1\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_1_9\\\"},{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_2\\\",\\\"name\\\":\\\"Hackathon-Coded_2\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"displayString\\\":\\\"Hackathon-Coded_2\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_2_9\\\"}]},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}]}\"}]}}");

        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);;

        List<String> codes = new ArrayList<String>();
        codes.add("Hackathon-Coded_1");
        codes.add("Hackathon-Coded_2");
        verify(bahmniPDFForm).beginSection("Section-1");
        verify(bahmniPDFForm).addCodedField("Hackathon-Coded_Parent", codes);
        verify(bahmniPDFForm).endSection();
    }

    @Test
    public void shouldParseJsonAndPrintObsGroupControl() throws IOException, DocumentException {
        parserImpl = new ParserImpl();
        JSONObject jsonObject = new JSONObject("{\"formJson\":{\"name\":\"HackathonForm\",\"version\":\"1\",\"published\":true,\"resources\":[{\"value\":\"{\\\"name\\\":\\\"HackathonForm\\\",\\\"defaultLocale\\\":\\\"en\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsGroupControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"OBS_GROUP_CONTROL_8\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"ObsGroupControl-1\\\",\\\"id\\\":\\\"8\\\"},\\\"id\\\":\\\"8\\\",\\\"controls\\\":[{\\\"type\\\":\\\"obsControl\\\",\\\"label\\\":{\\\"translationKey\\\":\\\"HACKATHON-CODED_PARENT_9\\\",\\\"id\\\":\\\"9\\\",\\\"units\\\":\\\"\\\",\\\"type\\\":\\\"label\\\",\\\"value\\\":\\\"Hackathon-Coded_Parent\\\"},\\\"properties\\\":{\\\"mandatory\\\":false,\\\"notes\\\":false,\\\"addMore\\\":false,\\\"hideLabel\\\":false,\\\"autoComplete\\\":false,\\\"multiSelect\\\":false,\\\"dropDown\\\":false},\\\"id\\\":\\\"9\\\",\\\"concept\\\":{\\\"name\\\":\\\"Hackathon-Coded_Parent\\\",\\\"datatype\\\":\\\"Coded\\\",\\\"conceptClass\\\":\\\"Misc\\\",\\\"answers\\\":[{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_1\\\",\\\"name\\\":\\\"Hackathon-Coded_1\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"displayString\\\":\\\"Hackathon-Coded_1\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_1_9\\\"},{\\\"name\\\":{\\\"display\\\":\\\"Hackathon-Coded_2\\\",\\\"name\\\":\\\"Hackathon-Coded_2\\\",\\\"locale\\\":\\\"en\\\",\\\"localePreferred\\\":true,\\\"conceptNameType\\\":\\\"FULLY_SPECIFIED\\\"},\\\"displayString\\\":\\\"Hackathon-Coded_2\\\",\\\"translationKey\\\":\\\"HACKATHON-CODED_2_9\\\"}]},\\\"units\\\":null,\\\"hiNormal\\\":null,\\\"lowNormal\\\":null,\\\"hiAbsolute\\\":null,\\\"lowAbsolute\\\":null}]}]}\"}]}}");
        parserImpl.jsonToPdfParser(bahmniPDFForm, jsonObject);;

        List<String> codes = new ArrayList<String>();
        codes.add("Hackathon-Coded_1");
        codes.add("Hackathon-Coded_2");
        verify(bahmniPDFForm).beginSection("ObsGroupControl-1");
        verify(bahmniPDFForm).addCodedField("Hackathon-Coded_Parent", codes);
        verify(bahmniPDFForm).endSection();
    }
}
