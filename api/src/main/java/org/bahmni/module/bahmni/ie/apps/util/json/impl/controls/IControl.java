package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import java.io.IOException;

public interface IControl {
    void print(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException;
}
