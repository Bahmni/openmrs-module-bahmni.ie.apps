package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import java.io.IOException;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.CONCEPT;
import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.NAME;

public class TextField implements IField {
    @Override
    public void addField(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException {
        String label = (String) ((JSONObject) control.get(CONCEPT)).get(NAME);
        bahmniPDFForm.addTextField(label);
    }
}
