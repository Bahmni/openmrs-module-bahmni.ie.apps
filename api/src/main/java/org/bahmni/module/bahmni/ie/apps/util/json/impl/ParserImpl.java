package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import org.bahmni.module.bahmni.ie.apps.util.json.Parser;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;

@Service("jsonParser")
public class ParserImpl implements Parser {

    @Override
    public String jsonToPdfParser(BahmniPDFFormImpl bahmniPDFForm, JSONObject jsonObject) throws IOException {
        String title = (String) ((JSONObject) jsonObject.get(FORM_JSON)).get(NAME);
        bahmniPDFForm.setTitle(title);

        JSONArray resources = (JSONArray) jsonObject.get(RESOURCES);
        JSONObject resource = (JSONObject) resources.get(0);
        JSONArray controls = (JSONArray) ((JSONObject) resource.get(VALUE)).get(CONTROLS);

        for (int i = 0; i < controls.length(); i++) {
            JSONObject control = (JSONObject) controls.get(i);
            if (control.get(TYPE).equals(LABEL)) {
                printLabelToPdf(bahmniPDFForm, control);
            }
            if (control.get(TYPE).equals(OBS_CONTROL)) {
                printObsControlToPdf(bahmniPDFForm, control);
            }
            if (control.get(TYPE).equals(SECTION)) {
                printSectionToPdf(bahmniPDFForm, control);
            }
        }
        return bahmniPDFForm.create();
    }

    void printLabelToPdf(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        bahmniPDFForm.addLabel((String) control.get(VALUE));
    }

    void printObsControlToPdf(BahmniPDFFormImpl bahmniPDFForm, JSONObject control) {
        String datatype = (String) ((JSONObject) control.get(CONCEPT)).get(DATATYPE);
        String controlLabel = (String) ((JSONObject) control.get(CONCEPT)).get(NAME);
        if (datatype.equals(TEXT)) {
            bahmniPDFForm.addTextField(controlLabel);
        }
        if (datatype.equals(NUMERIC)) {
            JSONObject label = (JSONObject) control.get(LABEL);
            String unit = (String) label.get(UNITS);
            bahmniPDFForm.addNumericField(controlLabel, unit);
        }
        if (datatype.equals(DATE_TIME)) {
            bahmniPDFForm.addDateTimeField(controlLabel);
        }
        if (datatype.equals(BOOLEAN)) {
            bahmniPDFForm.addBooleanField(controlLabel);
        }
        if (datatype.equals(CODED)) {
            JSONArray answers = (JSONArray) ((JSONObject) control.get(CONCEPT)).get(ANSWERS);
            List<String> codes = new ArrayList<>();
            for (int i = 0; i < answers.length(); i++) {
                codes.add((String) ((JSONObject) ((JSONObject) answers.get(i)).get(NAME)).get(DISPLAY));
            }
            bahmniPDFForm.addCodedField(controlLabel, codes);
        }
    }

    void printSectionToPdf(BahmniPDFFormImpl bahmniPDFForm, JSONObject control) {
        String sectionTitle = (String) ((JSONObject) control.get(LABEL)).get(VALUE);
        bahmniPDFForm.beginSection(sectionTitle);
        JSONArray sectionControls = (JSONArray) control.get(CONTROLS);

        for (int i = 0; i < sectionControls.length(); i++) {
            JSONObject sectionControl = (JSONObject) sectionControls.get(i);
            if (sectionControl.get(TYPE).equals(LABEL)) {
                printLabelToPdf(bahmniPDFForm, sectionControl);
            }
            if (sectionControl.get(TYPE).equals(OBS_CONTROL)) {
                printObsControlToPdf(bahmniPDFForm, sectionControl);
            }
            if (sectionControl.get(TYPE).equals(SECTION)) {
                printSectionToPdf(bahmniPDFForm, sectionControl);
            }
        }
        bahmniPDFForm.endSection();
    }
}
