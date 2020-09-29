package org.bahmni.module.bahmni.ie.apps.util.json.impl;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.json.Parser;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service("jsonParser")
public class ParserImpl implements Parser {

    private final String FORM_JSON = "formJson";

    @Override
    public void jsonToPdfParser(BahmniPDFFormImpl bahmniPDFForm, JSONObject jsonObject) throws IOException, DocumentException{
        String title = (String) ((JSONObject) jsonObject.get(FORM_JSON)).get("name");
        bahmniPDFForm.setTitle(title);
    }
}
