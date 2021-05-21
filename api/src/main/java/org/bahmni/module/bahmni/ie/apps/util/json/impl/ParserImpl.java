package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.json.Parser;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.IControl;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.*;
import static org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.mapper.ControlMapper.stringToControlMap;

@Service("jsonParser")
public class ParserImpl implements Parser {

    @Override
    public String jsonToPdfParser(BahmniPDFFormImpl bahmniPDFForm, JSONObject jsonObject) throws IOException, DocumentException {
        String title = (String) ((JSONObject) jsonObject.get(FORM_JSON)).get(NAME);
        bahmniPDFForm.setTitle(title);

        JSONArray resources = (JSONArray) ((JSONObject) jsonObject.get(FORM_JSON)).get(RESOURCES);
        JSONObject resource = (JSONObject) resources.get(0);
        String resourceString = resource.getString(VALUE);
        JSONObject resourceObject = new JSONObject(resourceString);
        JSONArray controls = (JSONArray) resourceObject.get(CONTROLS);

        for (int i = 0; i < controls.length(); i++) {
            JSONObject controlJSON = (JSONObject) controls.get(i);
            IControl control = stringToControlMap.get(controlJSON.get(TYPE));
            control.print(bahmniPDFForm, controlJSON);
        }
        return bahmniPDFForm.create();
    }
}
