package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.json.Parser;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service("jsonParser")
public class ParserImpl implements Parser {

    private final String RESOURCES = "resources";
    private final String CONTROLS = "controls";
    private final String VALUE = "value";
    private final String TYPE = "type";
    private final String LABEL = "label";
    private final String FORM_JSON = "formJson";
    private final String OBS_CONTROL = "obsControl";
    private final String CONCEPT = "concept";

    @Override
    public void jsonToPdfParser(BahmniPDFFormImpl bahmniPDFForm, JSONObject jsonObject) throws IOException, DocumentException{
        String title = (String) ((JSONObject) jsonObject.get(FORM_JSON)).get("name");
        bahmniPDFForm.setTitle(title);
        bahmniPDFForm.create();

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
        }
    }

    void printLabelToPdf(BahmniPDFForm bahmniPDFForm, JSONObject control){
        bahmniPDFForm.addLabel((String) control.get(VALUE));
    }

    void printObsControlToPdf(BahmniPDFFormImpl bahmniPDFForm, JSONObject control){
        String datatype = (String) ((JSONObject) control.get(CONCEPT)).get("datatype");
        String controlLabel = (String) ((JSONObject) control.get(CONCEPT)).get("name");
        if(datatype.equals("Text")){
            bahmniPDFForm.addTextField(controlLabel);
        }
        if(datatype.equals("Numeric")){
            JSONObject label = (JSONObject) control.get(LABEL);
            String unit = (String) label.get("units");
            bahmniPDFForm.addNumericField(controlLabel, unit);
        }
        if(datatype.equals("Datetime")){
            bahmniPDFForm.addDateTimeField(controlLabel);
        }
    }
}
