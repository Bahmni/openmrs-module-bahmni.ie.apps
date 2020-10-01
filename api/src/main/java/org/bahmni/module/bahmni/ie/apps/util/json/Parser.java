package org.bahmni.module.bahmni.ie.apps.util.json;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Parser {
    String jsonToPdfParser(BahmniPDFFormImpl bahmniPDFForm, JSONObject jsonObject) throws IOException, DocumentException;
}
