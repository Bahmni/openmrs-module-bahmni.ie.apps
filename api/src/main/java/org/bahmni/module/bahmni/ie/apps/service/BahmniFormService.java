/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.service;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormSearchParams;
import org.bahmni.module.bahmni.ie.apps.model.ExportResponse;
import org.openmrs.Form;
import org.openmrs.api.OpenmrsService;

import java.util.List;

public interface BahmniFormService extends OpenmrsService {

    BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource);

    BahmniForm publish(String formUuid);

    List<BahmniForm> getAllLatestPublishedForms(BahmniFormSearchParams searchParams);

    List<BahmniForm> getAllForms();

    ExportResponse formDetailsFor(List<String> formUuids);

    BahmniFormResource saveFormNameTranslation(BahmniFormResource bahmniFormResource, String referenceFormUuid);

    Form getFormDetailsFromFormName(String formName, String formVersion);

    Form getFormsForGivenUuid(String formUuid);
}
