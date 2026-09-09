/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.helper;

import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;

import java.util.HashMap;

public class FormTranslationHelper {
    public static FormTranslation createFormTranslation(String locale, String version, String formName) {
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

    public static FormTranslation createFormTranslation(String locale, String version, String formUuid, String formName) {
        FormTranslation formTranslation = new FormTranslation();
        formTranslation.setLocale(locale);
        formTranslation.setVersion(version);
        formTranslation.setFormUuid(formUuid);
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

    public static FormTranslation createFormTranslation(String locale, String formUuid) {
        FormTranslation formTranslation = new FormTranslation();
        formTranslation.setLocale(locale);
        formTranslation.setFormUuid(formUuid);
        formTranslation.setFormName("form_name");
        formTranslation.setVersion("1");
        HashMap<String, String> concepts = new HashMap<>();
        concepts.put("TEMPERATURE_1", "Temperature");
        concepts.put("TEMPERATURE_1_DESC", "Temperature");
        formTranslation.setConcepts(concepts);
        HashMap<String, String> labels = new HashMap<>();
        labels.put("LABEL_2", "Vitals");
        formTranslation.setLabels(labels);
        return formTranslation;
    }


}
