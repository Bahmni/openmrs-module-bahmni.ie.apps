package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.apache.commons.io.FileUtils;
import org.bahmni.module.bahmni.ie.apps.validator.BahmniFormUtils;
import org.bahmni.module.bahmni.ie.apps.model.FormNameTranslation;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.bahmni.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class BahmniFormTranslationServiceImpl extends BaseOpenmrsService implements BahmniFormTranslationService {

	private FormService formService;

	private static final String DEFAULT_FORM_TRANSLATIONS_PATH = "/var/www/bahmni_config/openmrs/apps/forms/translations";

	private final String CONCEPT_TRANS_KEY_PATTERN = "_[0-9]+$";

	private final String DESC_TRANS_KEY_PATTERN = "_[0-9]+_DESC$";

	@Autowired
	public BahmniFormTranslationServiceImpl(FormService formService) {
		this.formService = formService;
	}

	public BahmniFormTranslationServiceImpl() {

	}

	@Override
	public List<FormTranslation> getFormTranslations(String formName, String formVersion,
													 String locale, String formUuid) {
		return translationsFor(locale, formName, formVersion, formUuid);
	}

	@Override
	@Transactional
	public List<FormTranslation> saveFormTranslation(List<FormTranslation> formTranslations) {
		ObjectMapper mapper = new ObjectMapper();
		List<FormTranslation> translationList =
				mapper.convertValue(formTranslations, new TypeReference<List<FormTranslation>>() {

				});
		FormTranslation firstTranslation = translationList.get(0);
		if (firstTranslation != null) {
			File translationFile = translationFileFor(firstTranslation);
			JSONObject translationsJson = translationFrom(translationList, translationFile);

			boolean hasReferenceForm = isNotEmpty(firstTranslation.getVersion())
					&& isNotEmpty(firstTranslation.getReferenceVersion());
			boolean referenceFormIsNotTheCurrentForm = hasReferenceForm &&
					!firstTranslation.getVersion().equals(firstTranslation.getReferenceVersion());

			if (hasReferenceForm && referenceFormIsNotTheCurrentForm) {
				File referenceVersionFile = translationFileFor(firstTranslation.getFormName(),
						firstTranslation.getReferenceVersion(), firstTranslation.getReferenceFormUuid());
				if(referenceVersionFile.exists()) {
					JSONObject refVersionTranslationsJson = existingTranslationsFrom(referenceVersionFile);
					if (!refVersionTranslationsJson.keySet().isEmpty())
						updateTranslationsWithRefVersion(firstTranslation, translationsJson, refVersionTranslationsJson);
				}
			}
			saveTranslationsToFile(translationsJson, translationFile);
		}

		return formTranslations;
	}

	@Override
	public FormFieldTranslations setNewTranslationsForForm(String locale, String formName, String version, String formUuid) {
		String defaultLocale = Context.getAdministrationService().getGlobalProperty("default_locale");
		FormTranslation defaultTranslation = translationsFor(defaultLocale, formName, version, formUuid).get(0);
		FormTranslation localeTranslation = translationsFor(locale, formName, version, formUuid).get(0);
		defaultTranslation = isEmpty(localeTranslation) ? defaultTranslation : localeTranslation;

		HashMap<String, ArrayList<String>> translatedConceptNames =
				getTranslationsForConcepts(Locale.forLanguageTag(locale), defaultTranslation.getConcepts(),
						Locale.forLanguageTag(defaultTranslation.getLocale()));
		Map<String, ArrayList<String>> translatedLabels = getLabelTranslations(locale, defaultTranslation.getLocale(),
				defaultTranslation.getLabels());

		return new FormFieldTranslations(translatedConceptNames, translatedLabels, locale);
	}

	@Override
	public String getFormNameTranslations(String formName, String uuid) {
		Form form = formService.getFormByUuid(uuid);
		FormResource formResource = formService.getFormResource(form, formName + "_FormName_Translation");
		return formResource != null ? formResource.getValueReference() : null;
	}

	private Map<String, ArrayList<String>> getLabelTranslations(String locale, String defaultLocale,
			Map<String, String> labels) {
		Stream<Map.Entry<String, String>> stream = labels.entrySet().stream();
		if (defaultLocale.equals(locale))
			return stream.collect(Collectors
					.toMap(Map.Entry::getKey, label -> new ArrayList<>(Collections.singletonList(label.getValue()))));
		return stream.collect(
				Collectors.toMap(Map.Entry::getKey, label -> new ArrayList<>(Collections.singletonList(label.getKey()))));
	}

	private JSONObject translationFrom(List<FormTranslation> translationList, File translationFile) {
		JSONObject translationsJson = existingTranslationsFrom(translationFile);
		for (FormTranslation formTranslation : translationList) {
			if (!validate(formTranslation)) {
				throw new APIException("Invalid Parameters");
			}
			translationsJson.put(formTranslation.getLocale(), getUpdatedTranslations(formTranslation));
		}
		return translationsJson;
	}

	private File translationFileFor(FormTranslation translation) {
		String formUuid = translation.getFormUuid();
		File translationFile = new File(getFileName(formUuid));
		translationFile.getParentFile().mkdirs();
		return translationFile;
	}

	private void updateTranslationsWithRefVersion(FormTranslation translation, JSONObject translationsJson,
												  JSONObject refVersionTranslationsJson) {
		Iterator<String> locales = refVersionTranslationsJson.keys();
		while (locales.hasNext()) {
			String locale = locales.next();
			boolean isDefaultLocale = locale.equals(translation.getLocale());
			JSONObject localeTranslations = refVersionTranslationsJson.getJSONObject(locale);
			localeTranslations.put("concepts",
					getUpdatedLocaleTranslationsForControls(localeTranslations.getJSONObject("concepts"),
							translation.getConcepts(), isDefaultLocale));
			localeTranslations.put("labels",
					getUpdatedLocaleTranslationsForControls(localeTranslations.getJSONObject("labels"),
							translation.getLabels(), isDefaultLocale));
			translationsJson.put(locale, localeTranslations);
		}
	}

	private Map<String, String> getUpdatedLocaleTranslationsForControls(JSONObject refVersionControls,
																		Map<String, String> currentVersionControls, boolean isDefualtLocale) {
		if (CollectionUtils.isEmpty(currentVersionControls))
			return new HashMap<>();
		currentVersionControls.keySet().forEach(control -> {
			if (refVersionControls.has(control))
				currentVersionControls.put(control, refVersionControls.getString(control));
			else
				currentVersionControls.put(control, isDefualtLocale ? currentVersionControls.get(control) : control);
		});
		return currentVersionControls;
	}

	private boolean isEmpty(FormTranslation formTranslation) {
		return formTranslation.getConcepts() == null && formTranslation.getLabels() == null;
	}

	private HashMap<String, ArrayList<String>> getTranslationsForConcepts(Locale locale,
			Map<String, String> conceptTranslations, Locale defaultLocale) {
		HashMap<String, ArrayList<String>> conceptWithAllNames = new HashMap<>();
		ConceptService conceptService = Context.getConceptService();
		for (String key : conceptTranslations.keySet()) {
			ArrayList<String> translations = getLocaleTranslations(locale, conceptTranslations, defaultLocale,
					conceptService, key);
			conceptWithAllNames.put(key, translations);
		}
		return conceptWithAllNames;
	}

	private ArrayList<String> getLocaleTranslations(Locale locale, Map<String, String> conceptTranslations,
			Locale defaultLocale, ConceptService conceptService, String key) {
		HashSet<String> translations = new HashSet<>();

		if (key.matches(String.format(".*%s", CONCEPT_TRANS_KEY_PATTERN)))
			translations.addAll(getConceptNames(locale, conceptService, key));
		else if (key.matches(String.format(".*%s", DESC_TRANS_KEY_PATTERN)))
			translations.addAll(getDescriptions(locale, conceptService, key));

		if (locale.equals(defaultLocale)) {
			ArrayList<String> translationsAsList = new ArrayList<>(translations);
			translationsAsList.remove(conceptTranslations.get(key));
			translationsAsList.add(0, conceptTranslations.get(key));
			return translationsAsList;
		}

		if (translations.isEmpty())
			translations.add(key);
		return new ArrayList<>(translations);
	}

	private String getFileName(String formUuid) {
		String fromTranslationsPath = Context.getAdministrationService()
				.getGlobalProperty("bahmni.formTranslations.directory", DEFAULT_FORM_TRANSLATIONS_PATH);
		return String.format("%s/%s.json", fromTranslationsPath, formUuid);
	}

	private File translationFileFor(String formName, String formVersion, String formUuid) {
		File file = null;
		if (isNotEmpty(formUuid))
			file = new File(getFileName(formUuid));
		if (file == null || !file.exists()) {
			file = new File(getFileName(formName, formVersion));
		}
        if (file == null || !file.exists()) {
            String normalizedFileName = BahmniFormUtils.normalizeFileName(formName);
            file = new File(getFileName(normalizedFileName, formVersion));
        }
		return file;
	}


	private String getFileName(String formName, String version) {
		String fromTranslationsPath = Context.getAdministrationService()
				.getGlobalProperty("bahmni.formTranslations.directory", DEFAULT_FORM_TRANSLATIONS_PATH);
		return String.format("%s/%s_%s.json", fromTranslationsPath, formName, version);
	}

	private void saveTranslationsToFile(JSONObject translationsJson, File translationFile) {
		try {
			FileUtils.writeStringToFile(translationFile, translationsJson.toString(), "UTF-8");
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new APIException(e.getMessage(), e);
		}
	}

	private JSONObject getUpdatedTranslations(FormTranslation formTranslation) {
		JSONObject translations = new JSONObject();
		translations.put("labels", new JSONObject(formTranslation.getLabels()));
		translations.put("concepts", new JSONObject(formTranslation.getConcepts()));
		return translations;
	}

	private boolean validate(FormTranslation formTranslation) {
		return isNotEmpty(formTranslation.getFormName()) &&
				isNotEmpty(formTranslation.getFormUuid()) &&
				isNotEmpty(formTranslation.getLocale()) &&
				isNotEmpty(formTranslation.getVersion());
	}

	private List<FormTranslation> translationsFor(String locale, String formName, String formVersion, String formUuid) {
		JSONObject jsonObject = getTranslationJsonFromFile(formName, formVersion, formUuid);
		List<FormTranslation> translationsList = new ArrayList<>();
		if (isNotEmpty(locale))
			translationsList.add(getParsedTranslations(jsonObject, locale, formName, formVersion));
		else
			for (String key : jsonObject.keySet())
				translationsList.add(getParsedTranslations(jsonObject, key, formName, formVersion));
		return translationsList;
	}

	private FormTranslation getParsedTranslations(JSONObject jsonObject, String locale, String formName,
			String formVersion) {
		FormTranslation formTranslation = FormTranslation.parse(jsonObject, locale);
		formTranslation.setFormName(formName);
		formTranslation.setVersion(formVersion);
		return formTranslation;
	}

	private JSONObject getTranslationJsonFromFile(String formName, String formVersion, String formUuid) {
		File translationFile = translationFileFor(formName, formVersion, formUuid);
		if (!translationFile.exists())
			throw new APIException(String.format("Unable to find translation file for %s_v%s", formName, formVersion));
		return existingTranslationsFrom(translationFile);
	}

	private JSONObject existingTranslationsFrom(File translationFile) {
		String fileContent;
		try {
			fileContent = translationFile.exists() ? FileUtils.readFileToString(translationFile, "UTF-8") : "";
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new APIException(e.getMessage(), e);
		}
		return isNotEmpty(fileContent) ? new JSONObject(fileContent) : new JSONObject();
	}

	private HashSet<String> getDescriptions(Locale locale, ConceptService conceptService, String key) {
		HashSet<String> descriptions = new HashSet<>();
		Concept concept = getConceptByName(conceptService, getDescConceptName(key));
		if (concept != null) {
			ConceptDescription conceptDescription = concept.getDescription(locale, true);
			if (conceptDescription != null) {
				descriptions.add(conceptDescription.getDescription());
			}
		}
		return descriptions;
	}

	private HashSet<String> getConceptNames(Locale locale, ConceptService conceptService, String key) {
		HashSet<String> conceptNames = new HashSet<>();
		String conceptName = getConceptNameFromKey(key);

		Concept concept = getConceptByName(conceptService, conceptName);
		if (concept != null) {
			addTranslatedName(conceptNames, concept.getName(locale, true));
			addTranslatedName(conceptNames, concept.getShortNameInLocale(locale));

			Collection<ConceptName> synonyms = concept.getSynonyms(locale);
			if (!synonyms.isEmpty())
				conceptNames.addAll(synonyms.stream().map(ConceptName::getName).collect(Collectors.toList()));
		}
		return conceptNames;
	}

	private void addTranslatedName(HashSet<String> conceptNames, ConceptName conceptName) {
		if (conceptName != null)
			conceptNames.add(conceptName.getName());
	}

	private Concept getConceptByName(ConceptService conceptService, String conceptName) {
		List<Concept> conceptsByName = conceptService.getConceptsByName(conceptName);
		for (Concept concept : conceptsByName) {
			ConceptName name = concept.getName();
			if (concept.getName().getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED) && name.getName()
					.equalsIgnoreCase(conceptName)) {
				return concept;
			}
		}
		return null;
	}

	private String getDescConceptName(String key) {
		return getString(key, DESC_TRANS_KEY_PATTERN);
	}

	private String getString(String key, String regexp) {
		return (key.replaceFirst(regexp, "")).replaceAll("_", " ");
	}

	private String getConceptNameFromKey(String key) {
		return getString(key, CONCEPT_TRANS_KEY_PATTERN);
	}

}
