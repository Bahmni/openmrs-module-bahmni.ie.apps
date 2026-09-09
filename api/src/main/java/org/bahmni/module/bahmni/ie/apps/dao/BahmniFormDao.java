/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.dao;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.db.DAOException;

import java.util.List;

public interface BahmniFormDao {

	List<Form> getAllForms(String formName, boolean includeRetired, boolean includeDraftState) throws DAOException;

	List<Form> getDraftFormByName(String name) throws DAOException;

	List<Form> getAllPublishedForms(boolean includeRetired) throws DAOException;

    List<BahmniForm> formsWithNameTransaltionsFor(String formName, boolean includeRetired,
												  boolean includeDraftState) throws DAOException;

    List<Form> getAllFormsByListOfUuids(List<String> formUuids) throws DAOException;

	List getAllPublishedFormsWithNameTranslation(boolean includeRetired) throws DAOException;

	Form getFormsForGivenUuid(String formUuid) throws DAOException;

}
