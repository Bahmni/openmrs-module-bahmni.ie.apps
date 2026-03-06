/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.service;

import org.bahmni.module.bahmni.ie.apps.model.FormNameTranslation;
import org.openmrs.api.OpenmrsService;
import org.bahmni.module.bahmni.ie.apps.model.FormFieldTranslations;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;

import java.util.List;

public interface BahmniFormTranslationService extends OpenmrsService {

	List<FormTranslation> getFormTranslations(String formName, String formVersion, String locale, String formUuid);

	List<FormTranslation> saveFormTranslation(List<FormTranslation> formTranslation);

	FormFieldTranslations setNewTranslationsForForm(String locale, String formName, String version, String formUuid);

	String getFormNameTranslations(String formName, String uuid);
}
