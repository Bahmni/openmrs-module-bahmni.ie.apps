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
