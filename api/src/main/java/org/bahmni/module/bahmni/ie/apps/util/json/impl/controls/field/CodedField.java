package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field;

import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;

public class CodedField implements IField {
    @Override
    public void addField(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        String label = (String) ((JSONObject) control.get(CONCEPT)).get(NAME);
        JSONArray answers = (JSONArray) ((JSONObject) control.get(CONCEPT)).get(ANSWERS);
        List<String> codes = new ArrayList<>();

        for (int i = 0; i < answers.length(); i++) {
            codes.add((String) ((JSONObject) ((JSONObject) answers.get(i)).get(NAME)).get(DISPLAY));
        }
        bahmniPDFForm.addCodedField(label, codes);
    }
}
