package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmni.ie.apps.validator.BahmniFormUtils;
import org.bahmni.module.bahmni.ie.apps.MotherForm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.bahmni.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.openmrs.customdatatype.NotYetPersistedException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.bahmni.module.bahmni.ie.apps.helper.FormTranslationHelper.createFormTranslation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@PrepareForTest({Context.class, BahmniFormUtils.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class BahmniFormTranslationServiceImplTest {

	@Mock
	FormService formService;

	@Mock
	private ConceptService conceptService;

	@Mock
	private AdministrationService administrationService;

	@Rule
	ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(administrationService);
	}

	@Test
	public void shouldFetchTranslationsForGivenLocaleWhenTranslationFileNameIsFormName() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "fr", "form_uuid");
		assertEquals(1, formTranslations.size());
		assertEquals("fr", formTranslations.get(0).getLocale());
		formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "en", "form_uuid");
		assertEquals(1, formTranslations.size());
		assertEquals("en", formTranslations.get(0).getLocale());
	}

	@Test
	public void shouldFetchTranslationsForGivenLocaleWhenTranslationFileNameIsFormUUID() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/form_uuid.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "fr", "form_uuid");
		assertEquals(1, formTranslations.size());
		assertEquals("fr", formTranslations.get(0).getLocale());
		formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", "en", "form_uuid");
		assertEquals(1, formTranslations.size());
		assertEquals("en", formTranslations.get(0).getLocale());
	}

	@Test
	public void shouldFetchAllTranslationsIfNoLocaleGiven() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		List<FormTranslation> formTranslations = bahmniFormTranslationService.getFormTranslations("test_form", "1", null, "form_uuid");
		assertEquals(2, formTranslations.size());
	}

	@Test
	public void shouldThrowAPIExceptionIfTranslationFileIsNotPresentForGivenFormNameAndVersionOrFormUUID() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		setTranslationPath("/var/www/blah/blah");
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unable to find translation file for test_form_v1");
		bahmniFormTranslationService.getFormTranslations("test_form", "1", "en", "form_uuid");
	}

	@Test
	public void shouldSaveTranslationsOfGivenForm() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();
		FormTranslation formTranslationEn = createFormTranslation("en",  "test_form");
		FormTranslation formTranslationFr = createFormTranslation("fr", "test_form");
		bahmniFormTranslationService
				.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn, formTranslationFr)));
		String expected =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}},"
						+
						"\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldThrowAPIExceptionIfFormUUIDIsNotPresentDuringSave() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		createTempFolder();
		FormTranslation formTranslation = createFormTranslation("en", null);
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Invalid Parameters");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Collections.singletonList(formTranslation)));
	}

	@Test(expected = APIException.class)
	public void shouldThrowAPIExceptionIfItUnableToSaveTranslations() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		FormTranslation formTranslation = createFormTranslation("en",  "test_form");
		setTranslationPath("/var/www/blah/blah");
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Directory '/var/www/blah/blah' could not be created");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Collections.singletonList(formTranslation)));
	}


	@Test
	public void shouldThrowAPIExceptionIfLocaleIsNotPresent() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		createTempFolder();
		FormTranslation formTranslation = createFormTranslation(null, "test_form");
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Invalid Parameters");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Collections.singletonList(formTranslation)));
	}

	@Test
	public void shouldGenerateTranslationsForGivenLocaleWithTranslationFileNameAsFromName() throws Exception {
		setupConceptMocks("en");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);
		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");

		assertEquals("fr", formFieldTranslations.getLocale());
		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(3, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1")
				.containsAll(Arrays.asList("Temperature fr", "Temp short", "TEMPERATURE")));
		assertFalse(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE DATA"));
		assertEquals(new ArrayList<>(Collections.singletonList("LABEL_2")),
				formFieldTranslations.getLabels().get("LABEL_2"));
		assertEquals("Temperature desc", conceptsWithAllName.get("TEMPERATURE_1_DESC").get(0));
		assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldGenerateTranslationsForGivenLocaleWithTranslationFileNameAsFromUuid() throws Exception {
		setupConceptMocks("en");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/formUuid.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);
		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");

		assertEquals("fr", formFieldTranslations.getLocale());
		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(3, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1")
				.containsAll(Arrays.asList("Temperature fr", "Temp short", "TEMPERATURE")));
		assertFalse(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE DATA"));
		assertEquals(new ArrayList<>(Collections.singletonList("LABEL_2")),
				formFieldTranslations.getLabels().get("LABEL_2"));
		assertEquals("Temperature desc", conceptsWithAllName.get("TEMPERATURE_1_DESC").get(0));
		assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldPutTranslationKeysAsTranslatedValueIfNoTranslationAvailableForGivenLocale() throws Exception {
		setupConceptMocks("en");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("es", "test_form", "1", "formUuid");

		assertEquals("es", formFieldTranslations.getLocale());
		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();
		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE_1"));

		assertEquals(new ArrayList<>(Collections.singletonList("LABEL_2")),
				formFieldTranslations.getLabels().get("LABEL_2"));
		assertEquals("TEMPERATURE_1_DESC", conceptsWithAllName.get("TEMPERATURE_1_DESC").get(0));
		assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldAddTranslationsForDefaultLocale() throws Exception {
		setupConceptMocks("fr");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");

		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals("fr", formFieldTranslations.getLocale());
		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(4, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1")
				.containsAll(Arrays.asList("Temperature fr", "Temp short", "TEMPERATURE", "Temperature")));

		assertEquals(new ArrayList<>(Collections.singletonList("Vitals")), formFieldTranslations.getLabels().get("LABEL_2"));
		assertTrue(
				conceptsWithAllName.get("TEMPERATURE_1_DESC").containsAll(Arrays.asList("Temperature desc", "Temperature")));
		assertEquals(2, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldAddAlreadySavedTranslationsForGivenLocale() throws Exception {
		setupConceptMocks("en");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Translated Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);
		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");

		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals("fr", formFieldTranslations.getLocale());
		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(4, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertEquals("Translated Temperature", conceptsWithAllName.get("TEMPERATURE_1").get(0));
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1")
				.containsAll(Arrays.asList("Translated Temperature", "Temperature fr", "Temp short", "TEMPERATURE")));

		assertEquals(new ArrayList<>(Collections.singletonList("Vitals")), formFieldTranslations.getLabels().get("LABEL_2"));
		assertTrue(
				conceptsWithAllName.get("TEMPERATURE_1_DESC").containsAll(Arrays.asList("Temperature desc", "Temperature")));
		assertEquals(2, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldAddAlreadySavedTranslationsForDefaultLocale() throws Exception {
		setupConceptMocks("fr");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);
		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");

		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals("fr", formFieldTranslations.getLocale());
		assertEquals(2, conceptsWithAllName.values().size());
		assertEquals(4, conceptsWithAllName.get("TEMPERATURE_1").size());
		assertEquals("Temperature", conceptsWithAllName.get("TEMPERATURE_1").get(0));
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1")
				.containsAll(Arrays.asList("Temperature", "Temperature fr", "Temp short", "TEMPERATURE")));

		assertEquals(new ArrayList<>(Collections.singletonList("Vitals")), formFieldTranslations.getLabels().get("LABEL_2"));
		assertTrue(
				conceptsWithAllName.get("TEMPERATURE_1_DESC").containsAll(Arrays.asList("Temperature desc", "Temperature")));
		assertEquals(2, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldNotAddKeysAsValueForConceptIfAtLeastOneTranslationForConceptNameFound() throws Exception {
		setupConceptMocks("pt");
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"pt\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("pt", "test_form", "1", "formUuid");
		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertEquals("pt", formFieldTranslations.getLocale());
		assertEquals(2, conceptsWithAllName.values().size());
		ArrayList<String> temperature_1 = conceptsWithAllName.get("TEMPERATURE_1");
		assertEquals(2, temperature_1.size());
		assertTrue(temperature_1.containsAll(Arrays.asList("TEMPERATURE", "Temperature")));
		assertFalse(temperature_1.contains("TEMPERATURE_1"));

		assertEquals(new ArrayList<>(Collections.singletonList("Vitals")), formFieldTranslations.getLabels().get("LABEL_2"));
		assertTrue(conceptsWithAllName.get("TEMPERATURE_1_DESC").contains("Temperature"));
		assertEquals(1, conceptsWithAllName.get("TEMPERATURE_1_DESC").size());
	}

	@Test
	public void shouldSetKeyAsValueIfConceptIsNotPresentForGivenLocale() throws Exception {
		setupConceptMocks("en");
		when(conceptService.getConceptsByName("TEMPERATURE")).thenReturn(new ArrayList<>());
		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String translationFilePath = createTempFolder() + "/test_form_1.json";
		String translationJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"" +
						"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";

		FileUtils.writeStringToFile(new File(translationFilePath), translationJson);

		FormFieldTranslations formFieldTranslations = bahmniFormTranslationService
				.setNewTranslationsForForm("fr", "test_form", "1", "formUuid");
		Map<String, ArrayList<String>> conceptsWithAllName = formFieldTranslations.getConcepts();

		assertTrue(conceptsWithAllName.get("TEMPERATURE_1").contains("TEMPERATURE_1"));
	}

	@Test
	public void shouldPersistOldVersionLocaleTranslationsToNewVersionWhenOldVersionTranslationFileNameIsFormName() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "2", "test_form_uuid", "test_form" );
		formTranslationEn.setReferenceVersion("1");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldPersistOldVersionLocaleTranslationsToNewVersionWhenOldVersionTranslationFileNameIsFormUUID() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String referenceFormUuid = "prev-uuid";
		String prevVersionTranslationsPath = tempTranslationsPath + "/" + referenceFormUuid + ".json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "2", "test_form_uuid", "test_form");
		formTranslationEn.setReferenceVersion("1");
		formTranslationEn.setReferenceFormUuid(referenceFormUuid);
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldReturnTranslationsInNewVersionWhenTheReferenceFormTranslationFileIsNotFound() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String referenceFormUuid = "prev-uuid";
		String prevVersionTranslationsPath = tempTranslationsPath + "/" + referenceFormUuid + "_not_found.json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "2", "test_form_uuid", "test_form");
		formTranslationEn.setReferenceVersion("1");
		formTranslationEn.setReferenceFormUuid(referenceFormUuid);
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));
		String expected ="{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldPersistApplicableOldVersionLocaleTranslationsToNewVersion() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);
		FormTranslation formTranslationEn = createFormTranslation("en", "2", "test_form_uuid", "test_form");
		formTranslationEn.setReferenceVersion("1");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1_DESC");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));
		String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}" +
				",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldGetNewConceptsTranslations() throws IllegalAccessException, NoSuchFieldException, IOException {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "test_form");
		formTranslationEn.setReferenceVersion("1");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1_DESC");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1");
		formTranslationEn.getConcepts().put("TEMPERATURE_2_DESC", "Temperature");
		formTranslationEn.getConcepts().put("TEMPERATURE_3_DESC", "Temperature");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_2_DESC\":\"Temperature\",\"TEMPERATURE_3_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form.json");
		assertTrue(translationFile.exists());
		assertEquals(FileUtils.readFileToString(translationFile), expected);
	}

	@Test
	public void shouldUpdateNewVersionLocaleTranslationsWithKey() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "2", "test_form_uuid", "test_form");
		formTranslationEn.setReferenceVersion("1");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1_DESC");
		formTranslationEn.getConcepts().put("TEMPERATURE_2_DESC", "Temperature");
		formTranslationEn.getConcepts().put("TEMPERATURE_3_DESC", "Temperature");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_2_DESC\":\"Temperature\",\"TEMPERATURE_3_DESC\":\"Temperature\",\"TEMPERATURE_1\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_2_DESC\":\"TEMPERATURE_2_DESC\",\"TEMPERATURE_3_DESC\":\"TEMPERATURE_3_DESC\",\"TEMPERATURE_1\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(expected, FileUtils.readFileToString(translationFile));
	}

	@Test
	public void shouldUpdateNewVersionLocaleTranslationsWithKeyFromOldVersion() throws Exception {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String oldVersionTranslationsPath = tempTranslationsPath + "/test_form_2.json";
		String oldVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(oldVersionTranslationsPath), oldVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "5", "test_form_uuid_5", "test_form");
		formTranslationEn.setReferenceVersion("2");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1_DESC");
		formTranslationEn.getConcepts().remove("TEMPERATURE_1");
		formTranslationEn.getLabels().remove("LABEL_2");

		formTranslationEn.getConcepts().put("TEMPERATURE_2_DESC", "Temperature");
		formTranslationEn.getConcepts().put("TEMPERATURE_3_DESC", "Temperature");
		formTranslationEn.getLabels().put("LABEL_1", "Blood");

		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_2_DESC\":\"Temperature\",\"TEMPERATURE_3_DESC\":\"Temperature\"},\"labels\":{\"LABEL_1\":\"Blood\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_2_DESC\":\"TEMPERATURE_2_DESC\",\"TEMPERATURE_3_DESC\":\"TEMPERATURE_3_DESC\"},\"labels\":{\"LABEL_1\":\"LABEL_1\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid_5.json");
		assertTrue(translationFile.exists());
		assertEquals(expected, FileUtils.readFileToString(translationFile));
	}

	@Test
	public void shouldSetEmptyTranslationsForConceptsWhenFormDoesnotHaveConcepts()
			throws IllegalAccessException, NoSuchFieldException, IOException {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/form_name_2.json";
		String prevVersionJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "3", "test_form_uuid","form_name");
		formTranslationEn.setReferenceVersion("2");
		formTranslationEn.setConcepts(null);
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected = "{\"en\":{\"concepts\":{},\"labels\":{\"LABEL_2\":\"Vitals\"}}" +
				",\"fr\":{\"concepts\":{},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form_uuid.json");
		assertTrue(translationFile.exists());
		assertEquals(expected, FileUtils.readFileToString(translationFile));
	}

	@Test
	public void shouldGetNormalTranslationsWhenPreviousVersionDoesnotHaveTranslations()
			throws IllegalAccessException, NoSuchFieldException, IOException {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String prevVersionTranslationsPath = tempTranslationsPath + "/test_form_2.json";
		String prevVersionJson = "{}";
		FileUtils.writeStringToFile(new File(prevVersionTranslationsPath), prevVersionJson);

		FormTranslation formTranslationEn = createFormTranslation("en", "test_form");
		formTranslationEn.setReferenceVersion("2");
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(formTranslationEn)));

		String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		File translationFile = new File(tempTranslationsPath + "/test_form.json");
		assertTrue(translationFile.exists());
		assertEquals(expected, FileUtils.readFileToString(translationFile));
	}

	@Test
	public void shouldCopyTranslationsFromImportedForm() throws IllegalAccessException, NoSuchFieldException, IOException {
		BahmniFormTranslationService bahmniFormTranslationService = new BahmniFormTranslationServiceImpl();
		String tempTranslationsPath = createTempFolder();

		String importedTranslationsPath = tempTranslationsPath + "/test_form_1.json";
		String importedTranslationsJson =
				"{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}"
						+
						",\"fr\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(importedTranslationsPath), importedTranslationsJson);

		FormTranslation defaultFormTranslations = createFormTranslation("en", "test_form");
		defaultFormTranslations.setReferenceVersion("1");
		HashMap<String, String> concepts = new HashMap<>();
		concepts.put("TEMPERATURE_1", "Some Default");
		concepts.put("TEMPERATURE_1_DESC", "Some Default");
		defaultFormTranslations.setConcepts(concepts);
		HashMap<String, String> labels = new HashMap<>();
		labels.put("LABEL_2", "Some Defaults");
		defaultFormTranslations.setLabels(labels);
		bahmniFormTranslationService.saveFormTranslation(new ArrayList<>(Arrays.asList(defaultFormTranslations)));

		File translationFile = new File(tempTranslationsPath + "/test_form_1.json");
		assertTrue(translationFile.exists());
		assertEquals(importedTranslationsJson, FileUtils.readFileToString(translationFile));
	}

	@Test
	public void shouldGetUnsupportedCharactersWithoutUTF8() throws IllegalAccessException, NoSuchFieldException, IOException {
		String tempTranslationsPath = createTempFolder();
		String translationsFilePath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature  Le caleçon\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(translationsFilePath), prevVersionJson, "ISO-8859-1");

		String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature  Le caleçon\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		assertNotEquals(FileUtils.readFileToString(new File(translationsFilePath)), expected);
	}

	@Test
	public void shouldSupportAllSpecialCharactersWithoutUTF8() throws IllegalAccessException, NoSuchFieldException, IOException {
		String tempTranslationsPath = createTempFolder();
		String translationsFilePath = tempTranslationsPath + "/test_form_1.json";
		String prevVersionJson = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature  Le caleçon\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		FileUtils.writeStringToFile(new File(translationsFilePath), prevVersionJson, "UTF-8");

		String expected = "{\"en\":{\"concepts\":{\"TEMPERATURE_1\":\"Temperature\",\"TEMPERATURE_1_DESC\":\"Temperature  Le caleçon\"},\"labels\":{\"LABEL_2\":\"Vitals\"}}}";
		assertEquals(FileUtils.readFileToString(new File(translationsFilePath)), expected);
	}

	@Test
	public void shouldThrowExceptionForNullValuedResource() {
		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
		FormResource formResource = MotherForm.createFormResource(1, "", "FormResourceUuid", form);

		when(formService.getFormByUuid("FormUuid")).thenReturn(form);
		when(formService.getFormResource(form, "FormName" + "_FormName_Translation")).thenReturn(formResource);

		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl(formService);

		expectedException.expect(NotYetPersistedException.class);

		bahmniFormTranslationService.getFormNameTranslations("FormName", "FormUuid");
	}

	@Test
	public void shouldGetFormNameTranslations() {
		String formName = "FormName";
		String formUuid = "FormUuid";

		Form form = MotherForm.createForm(formName, formUuid, "FormVersion", false);
		FormResource formResource = MotherForm.createFormResource(1, "THIS IS THE TRANSLATION VALUE", "FormResourceUuid", form);
		formResource.setValueReferenceInternal("THIS IS THE TRANSLATION VALUE");

		when(formService.getFormByUuid(formUuid)).thenReturn(form);
		when(formService.getFormResource(form, formName + "_FormName_Translation")).thenReturn(formResource);

		BahmniFormTranslationServiceImpl bahmniFormTranslationService = new BahmniFormTranslationServiceImpl(formService);

		String formNameTranslation = bahmniFormTranslationService.getFormNameTranslations(formName, formUuid);
		assertEquals("THIS IS THE TRANSLATION VALUE", formNameTranslation);
	}

	private String createTempFolder() throws IOException, NoSuchFieldException, IllegalAccessException {
		TemporaryFolder temporaryFolder = new TemporaryFolder();
		temporaryFolder.create();
		String translationsPath = temporaryFolder.getRoot().getAbsolutePath();
		return setTranslationPath(translationsPath);
	}

	private String setTranslationPath(String translationsPath) throws NoSuchFieldException, IllegalAccessException {
		when(administrationService.getGlobalProperty(eq("bahmni.formTranslations.directory"), Matchers.anyString()))
				.thenReturn(translationsPath);
		return translationsPath;
	}

	private void setupConceptMocks(String defaultLocale) {
		when(Context.getConceptService()).thenReturn(conceptService);
		when(Context.getLocale()).thenReturn(Locale.forLanguageTag(defaultLocale));

		ConceptName conceptName = new ConceptName("TEMPERATURE", Locale.ENGLISH);
		ConceptName conceptNamefr = new ConceptName("TEMPERATURE", Locale.FRENCH);
		ConceptName conceptNamept = new ConceptName("TEMPERATURE", Locale.forLanguageTag("pt"));
		ConceptName conceptShortName = new ConceptName("Temp short", Locale.FRENCH);
		ConceptName conceptNameFrench = new ConceptName("Temperature fr", Locale.FRENCH);
		ConceptDescription conceptDescription = new ConceptDescription("Temperature desc", Locale.FRENCH);
		ConceptDescription conceptDescriptionEn = new ConceptDescription("Temperature desc", Locale.ENGLISH);
		Concept concept = new Concept();
		conceptName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		conceptNamefr.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		concept.setNames(Arrays.asList(conceptName, conceptNameFrench, conceptShortName, conceptNamefr, conceptNamept));
		concept.setShortName(conceptShortName);
		concept.addDescription(conceptDescription);
		concept.addDescription(conceptDescriptionEn);

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
