package org.bahmni.module.bahmni.ie.apps.controller;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.ParserImpl;
import org.bahmni.module.bahmni.ie.apps.util.pdf.impl.BahmniPDFFormImpl;
import org.json.JSONObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class JsonToPdfController {
    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmniie/form";

    @Autowired
    ParserImpl parser;

    @RequestMapping(value = baseUrl + "/jsonToPdf", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> convert(@RequestBody String string) throws IOException, DocumentException {
        JSONObject jsonFile = new JSONObject(string);
        BahmniPDFFormImpl bahmniPDFForm = new BahmniPDFFormImpl();
        String generatedPdfDirPath = new String("/home/bahmni/pdf/");
        String gerneratedPdfName = parser.jsonToPdfParser(bahmniPDFForm, jsonFile).substring(generatedPdfDirPath.length());
        Map<String,String> response = new HashMap<>();
        response.put("pdfName",gerneratedPdfName);
        return response;
    }
}
