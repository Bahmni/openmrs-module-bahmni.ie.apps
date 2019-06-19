package org.openmrs.module.bahmni.ie.apps.model;

import org.json.JSONObject;

import java.util.Map;
import java.util.stream.Collectors;

public class FormTranslation {
    private String locale;
    private Map<String, String> labels;
    private Map<String, String> concepts;
    private String formName;
    private String version;
    private String referenceVersion;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getConcepts() {
        return concepts;
    }

    public void setConcepts(Map<String, String> concepts) {
        this.concepts = concepts;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getReferenceVersion() {
        return referenceVersion;
    }

    public void setReferenceVersion(String referenceVersion) {
        this.referenceVersion = referenceVersion;
    }

    public static FormTranslation parse(JSONObject jsonObject, String locale) {
        FormTranslation formTranslation = new FormTranslation();
        if (!jsonObject.has(locale))
            return formTranslation;
        JSONObject translations = (JSONObject) jsonObject.get(locale);

        JSONObject conceptsObj = (JSONObject) translations.get("concepts");
        JSONObject labelsObj = (JSONObject) translations.get("labels");
        Map<String, String> concepts = conceptsObj.keySet().stream().collect(Collectors.toMap(k -> k, conceptsObj::getString));
        Map<String, String> labels = labelsObj.keySet().stream().collect(Collectors.toMap(k -> k, labelsObj::getString));

        formTranslation.setLocale(locale);
        formTranslation.setConcepts(concepts);
        formTranslation.setLabels(labels);
        return formTranslation;
    }
}
