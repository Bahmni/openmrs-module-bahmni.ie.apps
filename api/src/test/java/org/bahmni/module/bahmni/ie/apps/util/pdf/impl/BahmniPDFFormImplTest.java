package org.bahmni.module.bahmni.ie.apps.util.pdf.impl;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
public class BahmniPDFFormImplTest {
    String title = "Bahmni Form";
    BahmniPDFFormImpl bahmniPDFForm = new BahmniPDFFormImpl(title);

    @Test
    public void shouldCreatePdfForm() throws FileNotFoundException, DocumentException {
        Document document = mock(Document.class);
        Whitebox.setInternalState(bahmniPDFForm, Document.class, document);

        bahmniPDFForm.create();

        verify(document).open();
        verify(document).add(any(Chunk.class));
        verify(document).close();
    }

    @Test
    public void shouldAddTextField() throws IOException, DocumentException {
        bahmniPDFForm.addTextField("random");
        PdfReader reader = new PdfReader("BahmniForm.pdf");
        String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);

        assertThat(textFromPage, containsString("random"));
    }
}