package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;

public class ObsControl implements IControl {
    @Override
    public void print(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException {
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
}
