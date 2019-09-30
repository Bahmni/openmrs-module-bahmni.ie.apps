package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.bahmni.customdatatype.datatype.FileSystemStorageDatatype;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.Obs;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormDao;
import org.bahmni.module.bahmni.ie.apps.mapper.BahmniFormMapper;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormService;
import org.bahmni.module.bahmni.ie.apps.validator.BahmniFormUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service("bahmniFormService")
public class BahmniFormServiceImpl extends BaseOpenmrsService implements BahmniFormService {

	private FormService formService;

	private BahmniFormDao bahmniFormDao;

	private AdministrationService administrationService;

	private final Integer DEFAULT_VERSION = 1;

	private final String DEFAULT_JSON_FOLDER_PATH = "/home/bahmni/clinical_forms/";

	private final String GP_BAHMNI_FORM_PATH_JSON = "bahmni.forms.directory";

	@Autowired
	public BahmniFormServiceImpl(FormService formService, BahmniFormDao bahmniFormDao,
			@Qualifier("adminService") AdministrationService administrationService) {
		this.formService = formService;
		this.bahmniFormDao = bahmniFormDao;
		this.administrationService = administrationService;
	}

	public BahmniFormServiceImpl() {
	}

	@Override
	@Transactional
	public BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource) {
		Form form = formService.getFormByUuid(bahmniFormResource.getForm().getUuid());
		FormResource formResource = getFormResource(bahmniFormResource.getUuid());
		if (form.getPublished()) {
			form = cloneForm(form);
			form.setVersion(nextGreatestVersionId(form.getName()).toString());
			formService.saveForm(form);

			formResource = cloneFormResource(formResource);
		}
		formResource.setForm(form);
		formResource.setName(bahmniFormResource.getForm().getName());
		formResource.setDatatypeClassname(FileSystemStorageDatatype.class.getName());
		formResource.setDatatypeConfig(constructFileNameFromForm(form));
		formResource.setValue(bahmniFormResource.getValue());
		formResource = formService.saveFormResource(formResource);
		return new BahmniFormMapper().map(formResource);
	}

	private String constructFileNameFromForm(Form form) {
		String fileName = BahmniFormUtils.normalizeFileName(form.getName()) + "_" + form.getVersion() + ".json";
		return administrationService.getGlobalProperty(GP_BAHMNI_FORM_PATH_JSON, DEFAULT_JSON_FOLDER_PATH) + fileName;
	}

	@Override
	@Transactional
	public BahmniForm publish(String formUuid) {
		Form form = formService.getFormByUuid(formUuid);
		if (form != null) {
			Integer nextVersionNumber = nextGreatestVersionId(form.getName());
			if (Integer.parseInt(form.getVersion()) + 1 != nextVersionNumber) {
				form.setVersion(nextVersionNumber.toString());
			}
			form.setPublished(Boolean.TRUE);
			form = formService.saveForm(form);
			updateFormResourceWithLatestVersion(form);
		}
		return new BahmniFormMapper().map(form);
	}

	private void updateFormResourceWithLatestVersion(Form form) {
		Collection<FormResource> formResourceCollection = formService.getFormResourcesForForm(form);
		if (formResourceCollection.size() == 1) {
			FormResource formResource = formResourceCollection.iterator().next();
			formResource.setDatatypeClassname(FileSystemStorageDatatype.class.getName());
			formResource.setDatatypeConfig(constructFileNameFromForm(form));
			formResource.setValue(formResource.getValue());
			formService.saveFormResource(formResource);
		}
	}

	@Override
	public List<BahmniForm> getAllLatestPublishedForms(boolean includeRetired, String encounterUuid) {
		List<Form> allPublishedForms = bahmniFormDao.getAllPublishedForms(includeRetired);
		List<BahmniForm> latestPublishedForms = getLatestFormByVersion(allPublishedForms);

		if (encounterUuid == null) {
			return latestPublishedForms;
		}

		Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		Set<Obs> obs = encounter.getAllObs(false);
		if (CollectionUtils.isEmpty(obs)) {
			return latestPublishedForms;
		}

		Map<String, List<Obs>> groupedObsByFormName = obs.parallelStream().filter(o -> o.getFormFieldPath() != null)
				.collect(Collectors.groupingByConcurrent(BahmniFormServiceImpl::getKey));
		if (MapUtils.isEmpty(groupedObsByFormName)) {
			return latestPublishedForms;
		}

		return mergeForms(allPublishedForms, latestPublishedForms, groupedObsByFormName);
	}

	@Override
	public List<BahmniForm> getAllForms() {
		List<Form> formList = bahmniFormDao.getAllForms(null, false, false);
		List<BahmniForm> bahmniFormList = new ArrayList<>();
		BahmniFormMapper mapper = new BahmniFormMapper();
		for (Form form : formList) {
			bahmniFormList.add(mapper.map(form));
		}
		return bahmniFormList;
	}

	private List<BahmniForm> mergeForms(List<Form> allPublishedForms, List<BahmniForm> latestPublishedForms,
			Map<String, List<Obs>> groupedObsByFormName) {
		for (String formName : groupedObsByFormName.keySet()) {
			String[] formNameAndVersion = formName.split("\\.");
			boolean isSameVersion = latestPublishedForms.parallelStream()
					.anyMatch(isSameBahmniForm(formNameAndVersion));
			if (!isSameVersion) {
				List<Form> listForms = allPublishedForms.stream().filter(isSameForm(formNameAndVersion))
						.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(listForms))
					continue;
				Form publishedFormWithObs = listForms.get(0);
				latestPublishedForms = latestPublishedForms.parallelStream().map(form -> {
					if (form.getName().equals(formNameAndVersion[0])) {
						form.setVersion(publishedFormWithObs.getVersion());
						form.setUuid(publishedFormWithObs.getUuid());
					}
					return form;
				}).collect(Collectors.toList());
			}
		}
		return latestPublishedForms;
	}

	private Predicate<Form> isSameForm(String[] formNameAndVersion) {
		return form -> form.getName().equals(formNameAndVersion[0]) && form.getVersion().equals(formNameAndVersion[1]);
	}

	private Predicate<BahmniForm> isSameBahmniForm(String[] formNameAndVersion) {
		return form -> form.getName().equals(formNameAndVersion[0]) && form.getVersion().equals(formNameAndVersion[1]);
	}

	private static String getKey(Obs o) {
		return o.getFormFieldPath().split("/")[0];
	}

	private List<BahmniForm> getLatestFormByVersion(List<Form> forms) {
		Map<String, Form> bahmniFormMap = new LinkedHashMap<>();
		if (CollectionUtils.isNotEmpty(forms)) {
			for (Form form : forms) {
				String formName = form.getName();
				if (bahmniFormMap.containsKey(formName)) {
					if (Integer.parseInt(form.getVersion()) > Integer.parseInt(bahmniFormMap.get(formName).getVersion())) {
						bahmniFormMap.put(formName, form);
					}
				} else {
					bahmniFormMap.put(formName, form);
				}
			}
		}
		return map(bahmniFormMap);
	}

	private List<BahmniForm> map(Map<String, Form> forDetailsMap) {
		List<BahmniForm> bahmniForms = new ArrayList<>();
		BahmniFormMapper mapper = new BahmniFormMapper();
		if (MapUtils.isNotEmpty(forDetailsMap)) {
			for (Form form : forDetailsMap.values()) {
				bahmniForms.add(mapper.map(form));
			}
		}
		return bahmniForms;
	}

	private FormResource getFormResource(String formResourceUuid) {
		FormResource formResource = formService.getFormResourceByUuid(formResourceUuid);
		if (null == formResource) {
			formResource = new FormResource();
		}
		return formResource;
	}

	private Form cloneForm(Form form) {
		Form clonedForm = new Form();
		clonedForm.setName(form.getName());
		clonedForm.setVersion(form.getVersion());
		clonedForm.setBuild(form.getBuild());
		clonedForm.setEncounterType(form.getEncounterType());
		clonedForm.setCreator(form.getCreator());
		return clonedForm;
	}

	private FormResource cloneFormResource(FormResource formResource) {
		FormResource clonedFormResource = new FormResource();
		if (null != formResource.getId()) {
			clonedFormResource.setName(formResource.getName());
			clonedFormResource.setValue(formResource.getValue());
			clonedFormResource.setDatatypeClassname(formResource.getDatatypeClassname());
			clonedFormResource.setDatatypeConfig(formResource.getDatatypeConfig());
			clonedFormResource.setPreferredHandlerClassname(formResource.getPreferredHandlerClassname());
			clonedFormResource.setHandlerConfig(formResource.getHandlerConfig());
		}
		return clonedFormResource;
	}

	private Integer nextGreatestVersionId(String formName) {
		List<Form> forms = bahmniFormDao.getAllForms(formName, false, true);
		float version = 0f;
		if (CollectionUtils.isNotEmpty(forms)) {
			for (Form form : forms) {
				float formVersion = Float.parseFloat(form.getVersion());
				if (formVersion > version) {
					version = formVersion;
				}
			}
		}
		if (version > 0f) {
			version++;
			return (int) version;
		}
		return DEFAULT_VERSION;
	}
}
