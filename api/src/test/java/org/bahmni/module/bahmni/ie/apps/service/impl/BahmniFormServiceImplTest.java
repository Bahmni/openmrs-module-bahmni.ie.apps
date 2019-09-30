package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.bahmni.customdatatype.datatype.FileSystemStorageDatatype;
import org.bahmni.module.bahmni.ie.apps.service.impl.BahmniFormServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.bahmni.module.bahmni.ie.apps.MotherForm;
import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormDao;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class BahmniFormServiceImplTest {

	@Mock
	private FormService formService;

	@Mock
	private BahmniFormDao bahmniFormDao;

	@Mock
	private AdministrationService administrationService;

	private BahmniFormService service;

	@Mock
	private EncounterService encounterService;

	@Before
	public void setUp() {
		initMocks(this);
		mockStatic(Context.class);
		PowerMockito.when(Context.getEncounterService()).thenReturn(encounterService);
		service = new BahmniFormServiceImpl(formService, bahmniFormDao, administrationService);
	}

	@Test
	public void shouldSaveFormWithoutCreatingNewFormIfItIsInDraftState() {
		BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
		BahmniFormResource bahmniFormResource = MotherForm
				.createBahmniFormResource("FormResourceUuid", "ValueReference", bahmniForm);

		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
		FormResource formResource = MotherForm.createFormResource(1, "ValueReference", "FormResourceUuid", form);

		when(formService.getFormByUuid(any(String.class))).thenReturn(form);
		when(formService.getFormResourceByUuid(any(String.class))).thenReturn(formResource);
		when(formService.saveFormResource(any(FormResource.class))).thenReturn(formResource);

		BahmniFormResource updatedBahmniFormResource = service.saveFormResource(bahmniFormResource);

		Assert.assertNotNull(updatedBahmniFormResource);
		assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
		assertEquals("ValueReference", updatedBahmniFormResource.getValue());

		Assert.assertNotNull(updatedBahmniFormResource.getForm());
		assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
		assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
		assertEquals("FormVersion", updatedBahmniFormResource.getForm().getVersion());
		Assert.assertFalse(updatedBahmniFormResource.getForm().isPublished());
	}

	@Test
	public void shouldCreateNewFormIfItIsInPublishedState() {
		BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
		BahmniFormResource bahmniFormResource = MotherForm
				.createBahmniFormResource("FormResourceUuid", "ValueReference", bahmniForm);

		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
		FormResource formResource = MotherForm.createFormResource(1, "ValueReference", "FormResourceUuid", form);

		when(formService.getFormByUuid(any(String.class))).thenReturn(form);
		when(formService.getFormResourceByUuid(any(String.class))).thenReturn(formResource);
		when(formService.saveFormResource(any(FormResource.class))).thenReturn(formResource);

		BahmniFormResource updatedBahmniFormResource = service.saveFormResource(bahmniFormResource);

		Assert.assertNotNull(updatedBahmniFormResource);
		assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
		assertEquals("ValueReference", updatedBahmniFormResource.getValue());

		Assert.assertNotNull(updatedBahmniFormResource.getForm());
		assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
		assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
		assertEquals("FormVersion", updatedBahmniFormResource.getForm().getVersion());
		Assert.assertTrue(updatedBahmniFormResource.getForm().isPublished());
	}

	@Test
	public void shouldPublishForm() {
		Form form = MotherForm.createForm("FormName", "FormUuid", "1", true);
		when(formService.getFormByUuid(any(String.class))).thenReturn(form);
		when(formService.saveForm(form)).thenReturn(form);
		when(formService.getFormResourcesForForm(form)).thenReturn(new ArrayList<>());

		BahmniForm updatedBahmniForm = service.publish("FormUuid");

		Assert.assertNotNull(updatedBahmniForm);
		assertEquals("FormName", updatedBahmniForm.getName());
		assertEquals("FormUuid", updatedBahmniForm.getUuid());
		assertEquals("1", updatedBahmniForm.getVersion());
		Assert.assertTrue(updatedBahmniForm.isPublished());
	}

	@Test
	public void shouldPublishFormAndProvideTheLatestVersionIfTheVersionOfTheFormIsNotTheLatest() {
		Form publishedForm1 = MotherForm.createForm("FormName", "FormUuid3", "3", true);
		Form publishedForm2 = MotherForm.createForm("FormName", "FormUuid4", "4", true);
		Form publishedForm3 = MotherForm.createForm("FormName", "FormUuid5", "5", true);

		Form form = MotherForm.createForm("FormName", "FormUuid1", "1", false);

		when(formService.getFormByUuid(any(String.class))).thenReturn(form);

		when(bahmniFormDao.getAllForms(any(String.class), any(Boolean.class), any(Boolean.class)))
				.thenReturn(Arrays.asList(publishedForm1, publishedForm2, publishedForm3));
		when(formService.saveForm(form)).thenReturn(form);
		when(formService.getFormResourcesForForm(form)).thenReturn(new ArrayList<>());

		BahmniForm updatedBahmniForm = service.publish("FormUuid");

		Assert.assertNotNull(updatedBahmniForm);
		assertEquals("FormName", updatedBahmniForm.getName());
		assertEquals("FormUuid1", updatedBahmniForm.getUuid());
		assertEquals("6", updatedBahmniForm.getVersion());
		Assert.assertTrue(updatedBahmniForm.isPublished());
	}

	@Test
	public void shouldPublishFormAndShouldNotUpdateVersionIfTheVersionIsTheLatest() {
		Form publishedForm1 = MotherForm.createForm("FormName", "FormUuid1", "1", true);
		Form publishedForm2 = MotherForm.createForm("FormName", "FormUuid2", "2", true);

		Form form = MotherForm.createForm("FormName", "FormUuid3", "3", false);

		when(formService.getFormByUuid(any(String.class))).thenReturn(form);
		when(formService.saveForm(form)).thenReturn(form);
		when(formService.getFormResourcesForForm(form)).thenReturn(new ArrayList<>());

		when(bahmniFormDao.getAllForms(any(String.class), any(Boolean.class), any(Boolean.class)))
				.thenReturn(Arrays.asList(publishedForm1, publishedForm2));

		BahmniForm updatedBahmniForm = service.publish("FormUuid");

		Assert.assertNotNull(updatedBahmniForm);
		assertEquals("FormName", updatedBahmniForm.getName());
		assertEquals("FormUuid3", updatedBahmniForm.getUuid());
		assertEquals("3", updatedBahmniForm.getVersion());
		Assert.assertTrue(updatedBahmniForm.isPublished());
	}

	@Test
	public void shouldReturnAllLatestVersionOfPublishedForms() {
		Form form1 = MotherForm.createForm("FormName", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName", "FormUuid2", "2", true);
		Form form3 = MotherForm.createForm("FormName", "FormUuid3", "3", true);
		Form form4 = MotherForm.createForm("FormName", "FormUuid4", "4", true);
		when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));

		List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, null);

		Assert.assertNotNull(bahmniForms);
		assertEquals(1, bahmniForms.size());
		assertEquals("FormName", bahmniForms.get(0).getName());
		assertEquals("FormUuid4", bahmniForms.get(0).getUuid());
		assertEquals("4", bahmniForms.get(0).getVersion());
		Assert.assertTrue(bahmniForms.get(0).isPublished());
	}

	@Test
	public void shouldReturnLatestPublishedFormsIfNoObsAreRecorded() {
		Form form1 = MotherForm.createForm("FormName1", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName2", "FormUuid2", "1", true);
		Form form3 = MotherForm.createForm("FormName1", "FormUuid1", "2", true);
		Form form4 = MotherForm.createForm("FormName2", "FormUuid2", "2", true);

		Encounter encounter = new Encounter();
		when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));
		when(encounterService.getEncounterByUuid("encounterUuid")).thenReturn(encounter);

		List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, "encounterUuid");

		assertThat(bahmniForms.size(), is(2));
		assertThat(bahmniForms.get(0).getVersion(), is("2"));
		assertThat(bahmniForms.get(0).getName(), is("FormName1"));
		assertThat(bahmniForms.get(1).getVersion(), is("2"));
		assertThat(bahmniForms.get(1).getName(), is("FormName2"));
	}

	@Test
	public void shouldReturnLatestPublishedFormsIfObservationsDoNotHaveFormFieldPath() {
		Form form1 = MotherForm.createForm("FormName1", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName2", "FormUuid2", "1", true);
		Form form3 = MotherForm.createForm("FormName1", "FormUuid1", "2", true);
		Form form4 = MotherForm.createForm("FormName2", "FormUuid2", "2", true);

		Encounter encounter = new Encounter();
		encounter.setObs(new HashSet<>(Arrays.asList(new Obs(), new Obs())));

		when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));

		when(encounterService.getEncounterByUuid("encounterUuid")).thenReturn(encounter);

		List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, "encounterUuid");

		assertThat(bahmniForms.size(), is(2));
		assertThat(bahmniForms.get(0).getVersion(), is("2"));
		assertThat(bahmniForms.get(0).getName(), is("FormName1"));
		assertThat(bahmniForms.get(1).getVersion(), is("2"));
		assertThat(bahmniForms.get(1).getName(), is("FormName2"));
	}

	@Test
	public void shouldReturnLatestPublishedFormsIfFewObservationsHaveFormFieldPath() {
		Form form1 = MotherForm.createForm("FormName1", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName2", "FormUuid2", "1", true);
		Form form3 = MotherForm.createForm("FormName1", "FormUuid1", "2", true);
		Form form4 = MotherForm.createForm("FormName2", "FormUuid2", "2", true);

		Encounter encounter = new Encounter();

		Obs obs = new Obs();
		obs.setFormField("Bahmni", "FormName1.1/0");

		encounter.setObs(new HashSet<>(Arrays.asList(obs, new Obs())));

		when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));

		when(encounterService.getEncounterByUuid("encounterUuid")).thenReturn(encounter);

		List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, "encounterUuid");

		assertThat(bahmniForms.size(), is(2));
		assertThat(bahmniForms.get(0).getVersion(), is("1"));
		assertThat(bahmniForms.get(0).getName(), is("FormName1"));
		assertThat(bahmniForms.get(1).getVersion(), is("2"));
		assertThat(bahmniForms.get(1).getName(), is("FormName2"));
	}

	@Test
	public void shouldReturnAppropriatePublishedForms() {
		Form form1 = MotherForm.createForm("FormName1", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName2", "FormUuid2", "1", true);
		Form form3 = MotherForm.createForm("FormName1", "FormUuid1", "2", true);
		Form form4 = MotherForm.createForm("FormName2", "FormUuid2", "2", true);

		Encounter encounter = new Encounter();

		Obs obs1 = new Obs();
		obs1.setFormField("Bahmni", "FormName1.2/0");

		Obs obs2 = new Obs();
		obs2.setFormField("Bahmni", "FormName2.1/0");

		encounter.setObs(new HashSet<>(Arrays.asList(obs1, obs2)));

		when(bahmniFormDao.getAllPublishedForms(any(Boolean.class))).thenReturn(Arrays.asList(form1, form2, form3, form4));

		when(encounterService.getEncounterByUuid("encounterUuid")).thenReturn(encounter);

		List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, "encounterUuid");

		assertThat(bahmniForms.size(), is(2));
		assertThat(bahmniForms.get(0).getVersion(), is("2"));
		assertThat(bahmniForms.get(0).getName(), is("FormName1"));
		assertThat(bahmniForms.get(1).getVersion(), is("1"));
		assertThat(bahmniForms.get(1).getName(), is("FormName2"));
	}

	@Test
	public void ensureThatTheDataTypeParamsAreSetCorrectlyOnFormResource() {
		BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
		BahmniFormResource bahmniFormResource = MotherForm
				.createBahmniFormResource("FormResourceUuid", "ValueReference", bahmniForm);

		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
		FormResource formResource = MotherForm.createFormResource(1, "ValueReference", "FormResourceUuid", form);

		when(formService.getFormByUuid(any(String.class))).thenReturn(form);
		when(formService.getFormResourceByUuid(any(String.class))).thenReturn(formResource);

		formResource.setDatatypeClassname(FileSystemStorageDatatype.class.getName());
		formResource.setDatatypeConfig("FormName_FormVersion.json");

		when(formService.saveFormResource(formResource)).thenReturn(formResource);

		service.saveFormResource(bahmniFormResource);
		verify(formService).saveFormResource(formResource);
	}

	@Test
	public void shouldReturnAllForms() {
		Form form1 = MotherForm.createForm("FormName-1", "FormUuid1", "1", true);
		Form form2 = MotherForm.createForm("FormName-1", "FormUuid2", "2", true);
		Form form3 = MotherForm.createForm("FormName-2", "FormUuid3", "1", true);
		Form form4 = MotherForm.createForm("FormName-3", "FormUuid4", "1", false);
		when(bahmniFormDao.getAllForms(any(String.class), any(Boolean.class), any(Boolean.class)))
				.thenReturn(Arrays.asList(form1, form2, form3, form4));

		List<BahmniForm> bahmniForms = service.getAllForms();

		Assert.assertNotNull(bahmniForms);
		assertEquals(4, bahmniForms.size());
	}

}
