package org.bahmni.module.bahmni.ie.apps.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class DownloadPdf {
    private final String baseUrl = "/rest/" + RestConstants.VERSION_1 + "/bahmniie/form";

    @Autowired
    ResourceLoader resourceLoader;

    @RequestMapping(value = baseUrl + "/download/{fileName:.+}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<byte[]> download(@PathVariable("fileName") String fileName) throws IOException {
        Resource pdfFile = resourceLoader.getResource("file:" + "/home/bahmni/pdf/" + fileName);
        InputStream in = pdfFile.getInputStream();
        byte[] contents = new byte[in.available()];
        in.read(contents);

        HttpHeaders headers = new HttpHeaders();
        String pdfFileName = "form.pdf";
        headers.setContentDispositionFormData(pdfFileName, pdfFileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);

        return response;
    }
}
