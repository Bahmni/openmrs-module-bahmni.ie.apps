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
