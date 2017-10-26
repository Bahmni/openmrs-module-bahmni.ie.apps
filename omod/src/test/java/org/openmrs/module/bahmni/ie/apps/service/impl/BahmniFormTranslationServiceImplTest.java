package org.openmrs.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.openmrs.module.bahmni.ie.apps.model.FormTranslation;
import org.openmrs.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class BahmniFormTranslationServiceImplTest {

    @Mock
    private ConceptService conceptService;

    @Mock
    private AdministrationService administrationService;

    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFetchTranslationsForGivenLocale() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        FormTranslation formTranslationFr = createFormTranslation("fr", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);
        bahmniFormTranslationService.saveFormTranslation(formTranslationFr);

        List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "fr");
        assertEquals(1, formTranslations.size());
        assertEquals("fr", formTranslations.get(0).getLocale());
    }

    @Test
    public void shouldFetchAllTranslationsIfNoLocaleGiven() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        FormTranslation formTranslationFr = createFormTranslation("fr", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);
        bahmniFormTranslationService.saveFormTranslation(formTranslationFr);

        List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", null);
        assertEquals(2, formTranslations.size());
    }

    @Test
    public void shouldThrowAPIExceptionIfTranslationFileIsNotPresentForGivenFormNameAndVersion() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        setTranslationPath(bahmniFormTranslationService, "/var/www/blah/blah");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Unable to find translation file for test_form_v1");
        bahmniFormTranslationService.getFormTranslations("test_form", "1", "en");
    }

    @Test
    public void shouldSaveTranslationsOfGivenForm() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        String tempTranslationsPath = createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
        String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
        File translationFile = new File(tempTranslationsPath + "/test_form_1.json");
        assertTrue(translationFile.exists());
        assertEquals(FileUtils.readFileToString(translationFile), expected);
    }

    @Test
    public void shouldThrowAPIExceptionIfFormNameIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", "1", null);
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfItUnableToSaveTranslations() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        FormTranslation formTranslation = createFormTranslation("en", "1", "test_form");
        setTranslationPath(bahmniFormTranslationService, "/var/www/blah/blah");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("/test_form_1.json' could not be created");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfFormVersionIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation("en", null, "test_form");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldThrowAPIExceptionIfLocaleIsNotPresent() throws Exception {
        BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslation = createFormTranslation(null, "1", "test_form");
        expectedException.expect(APIException.class);
        expectedException.expectMessage("Invalid Parameters");
        bahmniFormTranslationService.saveFormTranslation(formTranslation);
    }

    @Test
    public void shouldGenerateTranslationsForGivenLocale() throws Exception {
        setupConceptMocks("en");
        BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);

        FormFieldTranslations formFieldTranslations = bahmniFormTranslationService.setNewTranslationsForForm("fr", "test_form", "1");

        assertEquals("fr", formFieldTranslations.getLocale());
        Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConceptsWithAllName();

        assertEquals(2, conceptsWithAllName.values().size());
        assertEquals(3, conceptsWithAllName.get("TEMPERATURE_1").size());
        assertTrue(conceptsWithAllName.get("TEMPERATURE_1").containsAll(Arrays.asList("Temperature fr", "Temp short", "TEMPERATURE")));
        assertFalse(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE DATA"));
        assertEquals("LABEL_2", formFieldTranslations.getLabelsWithAllName().get("LABEL_2"));
        assertEquals("Temperature desc", conceptsWithAllName.get("TEMPERATURE_1_DESC").get(0));
        assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
    }

    @Test
    public void shouldPutTranslationKeysAsTranslatedValueIfNoTranslationAvailableForGivenLocale() throws Exception {
        setupConceptMocks("en");
        BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);

        FormFieldTranslations formFieldTranslations = bahmniFormTranslationService.setNewTranslationsForForm("es", "test_form", "1");

        assertEquals("es", formFieldTranslations.getLocale());
        Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConceptsWithAllName();
        assertEquals(2, conceptsWithAllName.values().size());
        assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1").size());
        assertTrue(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE_1"));

        assertEquals("LABEL_2", formFieldTranslations.getLabelsWithAllName().get("LABEL_2"));
        assertEquals("TEMPERATURE_1_DESC", conceptsWithAllName.get("TEMPERATURE_1_DESC").get(0));
        assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
    }

    @Test
    public void shouldAddTranslationsForDefaultLocale() throws Exception {
        setupConceptMocks("fr");
        BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
        createTempFolder(bahmniFormTranslationService);
        FormTranslation formTranslationEn = createFormTranslation("fr", "1", "test_form");
        bahmniFormTranslationService.saveFormTranslation(formTranslationEn);

        FormFieldTranslations formFieldTranslations = bahmniFormTranslationService.setNewTranslationsForForm("fr", "test_form", "1");

        Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConceptsWithAllName();

        assertEquals("fr", formFieldTranslations.getLocale());
        assertEquals(2, conceptsWithAllName.values().size());
        assertEquals(4, conceptsWithAllName.get("TEMPERATURE_1").size());
        assertTrue(conceptsWithAllName.get("TEMPERATURE_1").containsAll(Arrays.asList("Temperature fr", "Temp short", "TEMPERATURE", "Temperature")));

        assertEquals("LABEL_2", formFieldTranslations.getLabelsWithAllName().get("LABEL_2"));
        assertTrue(conceptsWithAllName.get("TEMPERATURE_1_DESC").containsAll(Arrays.asList("Temperature desc", "Temperature")));
        assertEquals(2, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
    }

    private static FormTranslation createFormTranslation(String locale, String version, String formName) {
        FormTranslation formTranslation = new FormTranslation();
        formTranslation.setLocale(locale);
        formTranslation.setVersion(version);
        formTranslation.setFormName(formName);
        HashMap<String, String> concepts = new HashMap<>();
        concepts.put("TEMPERATURE_1", "Temperature");
        concepts.put("TEMPERATURE_1_DESC", "Temperature");
        formTranslation.setConcepts(concepts);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("LABEL_2", "Vitals");
        formTranslation.setLabels(labels);
        return formTranslation;
    }

    private static String createTempFolder(BahmniFormTranslationService bahmniFormTranslationService) throws IOException, NoSuchFieldException, IllegalAccessException {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.create();
        String translationsPath = temporaryFolder.getRoot().getAbsolutePath();
        return setTranslationPath(bahmniFormTranslationService, translationsPath);
    }

    private static String setTranslationPath(BahmniFormTranslationService bahmniFormTranslationService, String translationsPath) throws NoSuchFieldException, IllegalAccessException {
        Field field = bahmniFormTranslationService.getClass().getDeclaredField("FORM_TRANSLATIONS_PATH");
        field.setAccessible(true);
        field.set(bahmniFormTranslationService, translationsPath);
        return translationsPath;
    }

    private void setupConceptMocks(String defaultLocale) {
        PowerMockito.mockStatic(Context.class);
        when(Context.getConceptService()).thenReturn(conceptService);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(Context.getLocale()).thenReturn(Locale.forLanguageTag(defaultLocale));

        ConceptName conceptName = new ConceptName("TEMPERATURE", Locale.ENGLISH);
        ConceptName conceptNamefr = new ConceptName("TEMPERATURE", Locale.FRENCH);
        ConceptName conceptShortName = new ConceptName("Temp short", Locale.FRENCH);
        ConceptName conceptNameFrench = new ConceptName("Temperature fr", Locale.FRENCH);
        ConceptDescription conceptDescription = new ConceptDescription("Temperature desc", Locale.FRENCH);
        Concept concept = new Concept();
        conceptName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
        conceptNamefr.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
        concept.setNames(Arrays.asList(conceptName, conceptNameFrench, conceptShortName, conceptNamefr));
        concept.setShortName(conceptShortName);
        concept.addDescription(conceptDescription);

        ConceptName conceptNameOne = new ConceptName("TEMPERATURE DATA", Locale.ENGLISH);
        ConceptName conceptNamefrOne = new ConceptName("TEMPERATURE DATA", Locale.FRENCH);
        Concept concept1 = new Concept();
        conceptNamefrOne.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
        conceptNameOne.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
        concept1.setNames(Arrays.asList(conceptNamefrOne, conceptNameOne));

        when(conceptService.getConceptsByName("TEMPERATURE")).thenReturn(Arrays.asList(concept, concept1));
        when(administrationService.getGlobalProperty("default_locale")).thenReturn(defaultLocale);
    }
}