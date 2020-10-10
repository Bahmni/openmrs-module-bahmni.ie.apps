package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.VALUE;

public class LabelControl implements IControl {

    @Override
    public void print(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        bahmniPDFForm.addLabel((String) control.get(VALUE));
    }
}
