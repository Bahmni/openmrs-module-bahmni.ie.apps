/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.service;
import org.bahmni.module.bahmni.ie.apps.model.FormPrivilege;
import org.openmrs.api.OpenmrsService;
import java.util.List;

public interface BahmniFormPrivilegesService extends OpenmrsService {

    List<FormPrivilege> saveFormPrivileges(List<FormPrivilege> formPrivileges);

    List<FormPrivilege> getAllPrivilegesForForm(Integer formId , String formVersion);

    List<FormPrivilege> getFormPrivilegeGivenFormUuid(String formUuid, Integer formId);

    List<FormPrivilege> deleteAllPrivilegesForGivenFormId(Integer formId, String formVersion);

}
