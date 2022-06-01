package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.bahmni.customdatatype.datatype.FileSystemStorageDatatype;
import org.bahmni.module.bahmni.ie.apps.MotherForm;
import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormDao;
import org.bahmni.module.bahmni.ie.apps.mapper.BahmniFormMapper;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.ExportResponse;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.bahmni.module.bahmni.ie.apps.helper.FormTranslationHelper.createFormTranslation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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

    @Mock
    private BahmniFormTranslationService bahmniFormTranslationService;

    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(Context.class);
        PowerMockito.when(Context.getEncounterService()).thenReturn(encounterService);
        service = new BahmniFormServiceImpl(formService, bahmniFormDao, administrationService, bahmniFormTranslationService);
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

        assertNotNull(updatedBahmniFormResource);
        assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
        assertEquals("ValueReference", updatedBahmniFormResource.getValue());

        assertNotNull(updatedBahmniFormResource.getForm());
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

        assertNotNull(updatedBahmniFormResource);
        assertEquals("FormResourceUuid", updatedBahmniFormResource.getUuid());
        assertEquals("ValueReference", updatedBahmniFormResource.getValue());

        assertNotNull(updatedBahmniFormResource.getForm());
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

        assertNotNull(updatedBahmniForm);
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

        assertNotNull(updatedBahmniForm);
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

        assertNotNull(updatedBahmniForm);
        assertEquals("FormName", updatedBahmniForm.getName());
        assertEquals("FormUuid3", updatedBahmniForm.getUuid());
        assertEquals("3", updatedBahmniForm.getVersion());
        Assert.assertTrue(updatedBahmniForm.isPublished());
    }

    @Test
    public void shouldReturnAllLatestVersionOfPublishedForms() {
        BahmniForm form1 = MotherForm.createBahmniForm("FormName", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName", "FormUuid2", "2", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName", "FormUuid3", "3", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName", "FormUuid4", "4", true);
        when(bahmniFormDao.getAllPublishedFormsWithNameTranslation(any(Boolean.class)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));

        List<BahmniForm> bahmniForms = service.getAllLatestPublishedForms(false, null);

        assertNotNull(bahmniForms);
        assertEquals(1, bahmniForms.size());
        assertEquals("FormName", bahmniForms.get(0).getName());
        assertEquals("FormUuid4", bahmniForms.get(0).getUuid());
        assertEquals("4", bahmniForms.get(0).getVersion());
        Assert.assertTrue(bahmniForms.get(0).isPublished());
    }

    @Test
    public void shouldReturnLatestPublishedFormsIfNoObsAreRecorded() {
        BahmniForm form1 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "1", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "2", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "2", true);

        Encounter encounter = new Encounter();
        when(bahmniFormDao.getAllPublishedFormsWithNameTranslation(any(Boolean.class)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));
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
        BahmniForm form1 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "1", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "2", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "2", true);

        Encounter encounter = new Encounter();
        encounter.setObs(new HashSet<>(Arrays.asList(new Obs(), new Obs())));

        when(bahmniFormDao.getAllPublishedFormsWithNameTranslation(any(Boolean.class)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));

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
        BahmniForm form1 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "1", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "2", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "2", true);

        Encounter encounter = new Encounter();

        Obs obs = new Obs();
        obs.setFormField("Bahmni", "FormName1.1/0");

        encounter.setObs(new HashSet<>(Arrays.asList(obs, new Obs())));

        when(bahmniFormDao.getAllPublishedFormsWithNameTranslation(any(Boolean.class)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));

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
        BahmniForm form1 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "1", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName1", "FormUuid1", "2", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName2", "FormUuid2", "2", true);

        Encounter encounter = new Encounter();

        Obs obs1 = new Obs();
        obs1.setFormField("Bahmni", "FormName1.2/0");

        Obs obs2 = new Obs();
        obs2.setFormField("Bahmni", "FormName2.1/0");

        encounter.setObs(new HashSet<>(Arrays.asList(obs1, obs2)));

        when(bahmniFormDao.getAllPublishedFormsWithNameTranslation(any(Boolean.class)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));

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
        formResource.setDatatypeConfig("FormUuid_FormVersion.json");

        when(formService.saveFormResource(formResource)).thenReturn(formResource);

        service.saveFormResource(bahmniFormResource);
        verify(formService).saveFormResource(formResource);
    }

    @Test
    public void shouldReturnAllForms() {
        BahmniForm form1 = MotherForm.createBahmniForm("FormName-1", "FormUuid1", "1", true);
        BahmniForm form2 = MotherForm.createBahmniForm("FormName-1", "FormUuid2", "2", true);
        BahmniForm form3 = MotherForm.createBahmniForm("FormName-2", "FormUuid3", "1", true);
        BahmniForm form4 = MotherForm.createBahmniForm("FormName-3", "FormUuid4", "1", false);
        when(bahmniFormDao.formsWithNameTransaltionsFor(eq(null), eq(false), eq(false)))
                .thenReturn(Arrays.asList(form1, form2, form3, form4));

        List<BahmniForm> bahmniForms = service.getAllForms();

        assertNotNull(bahmniForms);
        assertEquals(4, bahmniForms.size());
    }

    @Test
    public void shouldReturnExportResponseForListOfUuidsPassed() {
        Form form1 = MotherForm.createForm("FormName-1", "FormUuid1", "1", true);
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setName("FormName-1");
        FormTranslation formTranslationEn = createFormTranslation("en", "1",
                "form_uuid", "FormName-1");
        FormTranslation formTranslationFr = createFormTranslation("fr", "1",
                "form_uuid", "FormName-1");
        FormResource formResourceOne = new FormResource();
        formResourceOne.setValue("Bahmni Form Resource one");
        BahmniFormResource bahmniFormResource = new BahmniFormResource();
        bahmniFormResource.setValue("Bahmni Form Resource one");
        BahmniFormMapper mapper = mock(BahmniFormMapper.class);

        when(bahmniFormDao.getAllFormsByListOfUuids(any())).thenReturn(Collections.singletonList(form1));
        when(bahmniFormTranslationService.getFormTranslations(form1.getName(), form1.getVersion(), null,
                form1.getUuid())).
                thenReturn(Arrays.asList(formTranslationEn, formTranslationFr));
        when(formService.getFormResourcesForForm(any(Form.class))).thenReturn(Collections.
                singletonList(formResourceOne));
        when(mapper.mapResources(any())).thenReturn(Collections.singletonList(bahmniFormResource));
        when(mapper.map(any(Form.class), any())).thenReturn(bahmniForm);

        ExportResponse response = service.formDetailsFor(Collections.singletonList("UUID1"));
        assertNotNull(response);
        assertEquals(1, response.getBahmniFormDataList().size());
        assertEquals(0, response.getErrorFormList().size());
        assertEquals(2, response.getBahmniFormDataList().get(0).getTranslations().size());
        assertEquals("en", response.getBahmniFormDataList().get(0).getTranslations().get(0).getLocale());
        assertEquals("fr", response.getBahmniFormDataList().get(0).getTranslations().get(1).getLocale());
        assertEquals("FormName-1", response.getBahmniFormDataList().get(0).getFormJson().getName());
        assertEquals("FormUuid1", response.getBahmniFormDataList().get(0).getFormJson().getUuid());
        assertEquals("1", response.getBahmniFormDataList().get(0).getFormJson().getVersion());
        assertEquals("Bahmni Form Resource one",
                response.getBahmniFormDataList().get(0).getFormJson().getResources().get(0).getValue());

    }

    @Test
    public void shouldReturnErrorFormNameWhenTranslationFileIsNotThereInThePath() throws Exception {
        Form form1 = MotherForm.createForm("FormName-1", "FormUuid1", "1", true);
        when(bahmniFormDao.getAllFormsByListOfUuids(any())).thenReturn(Collections.singletonList(form1));
        when(bahmniFormTranslationService.getFormTranslations(form1.getName(), form1.getVersion(),
                null, form1.getUuid())).thenAnswer( invocation -> { throw new Exception(); });
        ExportResponse response = service.formDetailsFor(Collections.singletonList("UUID"));
        assertNotNull(response);
        assertEquals(0, response.getBahmniFormDataList().size());
        assertEquals(1, response.getErrorFormList().size());
        assertEquals("[FormName-1_1]", response.getErrorFormList().toString());
    }

    @Test
    public void shouldReturnErrorFormNameWhenBahmniFormResourceIsNotThereInThePath() throws Exception {
        Form form1 = MotherForm.createForm("FormName-2", "FormUuid2", "1", true);
        FormTranslation formTranslationFr = createFormTranslation("fr", "1", "FormName-2");
        FormTranslation formTranslationEn = createFormTranslation("en", "1", "FormName-2");
        when(bahmniFormDao.getAllFormsByListOfUuids(any())).thenReturn(Collections.singletonList(form1));
        when(bahmniFormTranslationService.getFormTranslations(form1.getName(), form1.getVersion(), null, "form_uuid")).
                thenReturn(Arrays.asList(formTranslationEn, formTranslationFr));
        when(formService.getFormResourcesForForm(any(Form.class))).thenAnswer( invocation -> { throw new Exception(); });
        ExportResponse response = service.formDetailsFor(Collections.singletonList("UUID"));
        assertNotNull(response);
        assertEquals(0, response.getBahmniFormDataList().size());
        assertEquals(1, response.getErrorFormList().size());
        assertEquals("[FormName-2_1]", response.getErrorFormList().toString());
    }

    @Test
    public void shouldReturnBothFormDataAndErrorFormNameWhenOneOfTheFileDoesntHaveFormResource() throws Exception {
        BahmniFormMapper mapper = mock(BahmniFormMapper.class);
        Form form1 = MotherForm.createForm("FormName-1", "FormUuid1", "1", true);
        Form form2 = MotherForm.createForm("FormName-2", "FormUuid2", "1", true);
        FormTranslation formTranslationFr1 = createFormTranslation("fr", "1", "FormName-1");
        FormTranslation formTranslationEn1 = createFormTranslation("en", "1", "FormName-1");
        BahmniForm bahmniFormOne = new BahmniForm();
        bahmniFormOne.setName("FormName-1");
        BahmniForm bahmniFormTwo = new BahmniForm();
        bahmniFormTwo.setName("FormName-2");
        FormResource formResourceOne = new FormResource();
        formResourceOne.setValue("Bahmni Form Resource One");
        BahmniFormResource bahmniFormResourceOne = new BahmniFormResource();
        bahmniFormResourceOne.setValue("Bahmni Form Resource One");
        when(bahmniFormDao.getAllFormsByListOfUuids(any())).thenReturn(Arrays.asList(form1, form2));
        when(bahmniFormTranslationService.getFormTranslations(form1.getName(), form1.getVersion(), null,
                form1.getUuid())).thenReturn(Arrays.asList(formTranslationEn1, formTranslationFr1));
        when(bahmniFormTranslationService.getFormTranslations(form2.getName(), form2.getVersion(), null,
                form2.getUuid())).thenAnswer( invocation -> { throw new Exception(); });
        when(formService.getFormResourcesForForm(form1)).thenReturn(Collections.
                singletonList(formResourceOne));
        when(mapper.mapResources(Collections.singletonList(formResourceOne))).thenReturn(Collections.singletonList(bahmniFormResourceOne));
        when(mapper.map(any(Form.class), any())).thenReturn(bahmniFormOne);
        ExportResponse response = service.formDetailsFor(Arrays.asList("UUID1", "UUID2"));
        assertNotNull(response);
        assertEquals(1, response.getBahmniFormDataList().size());
        assertEquals(1, response.getErrorFormList().size());
        assertEquals("FormName-1", response.getBahmniFormDataList().get(0).getFormJson().getName());
        assertEquals("[FormName-2_1]", response.getErrorFormList().toString());
        assertEquals(2, response.getBahmniFormDataList().get(0).getTranslations().size());
        assertEquals("en", response.getBahmniFormDataList().get(0).getTranslations().get(0).getLocale());
        assertEquals("fr", response.getBahmniFormDataList().get(0).getTranslations().get(1).getLocale());
        assertEquals("FormUuid1", response.getBahmniFormDataList().get(0).getFormJson().getUuid());
        assertEquals("1", response.getBahmniFormDataList().get(0).getFormJson().getVersion());
        assertEquals("Bahmni Form Resource One", response.getBahmniFormDataList().get(0).getFormJson()
                .getResources().get(0).getValue());
    }


    @Test
    public void shouldSaveFormNameResourceWithGivenValue() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("", "ReferenceValue", bahmniForm);

        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
        FormResource formResource = MotherForm.createFormResource(1, "ReferenceValue", "FormResourceUuid", form);

        when(formService.getFormByUuid(any(String.class))).thenReturn(form);
        when(formService.saveFormResource(any(FormResource.class))).thenReturn(formResource);

        BahmniFormResource updatedBahmniFormResource = service.saveFormNameTranslation(bahmniFormResource, null);

        assertNotNull(updatedBahmniFormResource);
        assertEquals("ReferenceValue", updatedBahmniFormResource.getValue());

        assertNotNull(updatedBahmniFormResource.getForm());
        assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
        assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
    }

    @Test
    public void shouldSaveFormNameResourceWithAlreadyExistingValue() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("", "ReferenceValue", bahmniForm);

        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", false);
        Form oldForm = MotherForm.createForm("FormName", "OldFormUuid", "FormVersion", false);
        FormResource oldFormResource = MotherForm.createFormResource(1, "Old Form Reference Value", "FormResourceUuid", form);
        oldFormResource.setValueReferenceInternal("Old Form Reference Value");
        FormResource formResource = MotherForm.createFormResource(2, "ReferenceValue", "FormResourceUuid", form);

        when(formService.getFormByUuid("OldFormUuid")).thenReturn(oldForm);
        when(formService.getFormByUuid("FormUuid")).thenReturn(form);
        when(formService.getFormResource(eq(oldForm), any())).thenReturn(oldFormResource);
        when(formService.saveFormResource(any(FormResource.class))).thenReturn(oldFormResource);

        BahmniFormResource updatedBahmniFormResource = service.saveFormNameTranslation(bahmniFormResource, "OldFormUuid");

        assertNotNull(updatedBahmniFormResource);
        assertEquals("Old Form Reference Value", updatedBahmniFormResource.getValue());

        assertNotNull(updatedBahmniFormResource.getForm());
        assertEquals("FormName", updatedBahmniFormResource.getForm().getName());
        assertEquals("FormUuid", updatedBahmniFormResource.getForm().getUuid());
    }

    @Test
    public void shouldReturnNullIfNoValueAndReferenceFormUuidAreGiven() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("", "", bahmniForm);

        expectedException.expect(APIException.class);
        service.saveFormNameTranslation(bahmniFormResource, "");
        verify(formService, times(0)).saveFormResource(any());
    }

    @Test
    public void shouldReturnNullIfNoValueIsFoundForGivenReferenceFormUuid() {
        BahmniForm bahmniForm = MotherForm.createBahmniForm("FormName", "FormUuid");
        BahmniFormResource bahmniFormResource = MotherForm.createBahmniFormResource("", "", bahmniForm);

        Form oldForm = MotherForm.createForm("FormName", "OldFormUuid", "FormVersion", false);

        when(formService.getFormByUuid("OLD_REF_FORM_UUID")).thenReturn(oldForm);
        when(formService.getFormResource(oldForm, "FormName_FormName_Translation")).thenReturn(null);
        BahmniFormResource updatedBahmniFormResource = service.saveFormNameTranslation(bahmniFormResource, "OLD_REF_FORM_UUID");

        assertEquals("", updatedBahmniFormResource.getValue());
        verify(formService, times(0)).saveFormResource(any());
    }

}
