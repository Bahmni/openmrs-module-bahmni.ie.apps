package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;
import static org.bahmni.module.bahmni.ie.apps.util.json.impl.controlparser.ControlParser.stringToControlMap;

public class SectionControl implements IControl {

    @Override
    public void print(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException {

        String sectionTitle = (String) ((JSONObject) control.get(LABEL)).get(VALUE);
        bahmniPDFForm.beginSection(sectionTitle);
        JSONArray sectionControls = (JSONArray) control.get(CONTROLS);

        for (int i = 0; i < sectionControls.length(); i++) {
            JSONObject sectionControl = (JSONObject) sectionControls.get(i);
            stringToControlMap.get(sectionControl.get(TYPE)).print(bahmniPDFForm, sectionControl);
        }
        bahmniPDFForm.endSection();
    }
}
