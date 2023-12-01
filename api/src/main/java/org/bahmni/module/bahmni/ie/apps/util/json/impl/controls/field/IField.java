package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import java.io.IOException;

public interface IField {
    void addField(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException;
}
