package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field;

import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.CONCEPT;
import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.NAME;

public class DateField implements IField{

    @Override
    public void addField(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        String label = (String) ((JSONObject) control.get(CONCEPT)).get(NAME);
        bahmniPDFForm.addDateField(label);
    }
}
