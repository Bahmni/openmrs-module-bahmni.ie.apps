/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.dao;

import org.bahmni.module.bahmni.ie.apps.model.FormPrivilege;
import org.openmrs.api.db.DAOException;
import java.util.List;

public interface BahmniFormPrivilegeDao {

    List<FormPrivilege> getAllPrivilegesForForm(Integer formId , String formVersion) throws DAOException;

    FormPrivilege saveFormPrivilege(FormPrivilege formPrivilege) throws DAOException;

    FormPrivilege getFormPrivilege(String privilegeName , Integer formId) throws DAOException;

    List<FormPrivilege> getFormPrivilegeGivenFormUuid(String formVersion, Integer formId) throws DAOException;

    FormPrivilege deleteFormPrivilege(FormPrivilege formPrivilege) throws DAOException;
}
