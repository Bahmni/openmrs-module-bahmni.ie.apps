package org.bahmni.module.bahmni.ie.apps.util.pdf;

import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;

public interface BahmniPDFForm {

    String create() throws FileNotFoundException, DocumentException;
}
