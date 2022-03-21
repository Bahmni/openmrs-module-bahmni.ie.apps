package org.bahmni.module.bahmni.ie.apps.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormPrivilegeDao;
import org.bahmni.module.bahmni.ie.apps.model.FormPrivilege;
import org.bahmni.module.bahmni.ie.apps.model.FormTranslation;
import org.bahmni.module.bahmni.ie.apps.service.BahmniFormPrivilegesService;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.openmrs.User;
import org.openmrs.api.APIException;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("bahmniFormPrivilegesService")
public class BahmniFormPrivilegesServiceImpl extends BaseOpenmrsService implements BahmniFormPrivilegesService {

    private BahmniFormPrivilegeDao bahmniFormPrivilegeDao;
    protected Log log = LogFactory.getLog(getClass());
    private BahmniFormPrivilegesService bahmniFormPrivilegesService;

    @Autowired
    public BahmniFormPrivilegesServiceImpl(BahmniFormPrivilegeDao bahmniFormPrivilegeDao) {
        this.bahmniFormPrivilegeDao = bahmniFormPrivilegeDao;
        this.bahmniFormPrivilegesService = bahmniFormPrivilegesService;
    }
    public BahmniFormPrivilegesServiceImpl(){

    }

    public FormPrivilege saveFormPrivilege(FormPrivilege toPersistFormPrivilege) throws APIException {
        if (toPersistFormPrivilege != null) {
            FormPrivilege originalFormPrivilege ;
            originalFormPrivilege = getFormPrivilege(toPersistFormPrivilege.getPrivilegeName(),toPersistFormPrivilege.getFormId());
            if (originalFormPrivilege != null) {
                originalFormPrivilege.setEditable(toPersistFormPrivilege.getEditable());
                originalFormPrivilege.setViewable(toPersistFormPrivilege.getViewable());
                toPersistFormPrivilege = originalFormPrivilege;
            }
            return bahmniFormPrivilegeDao.saveFormPrivilege(toPersistFormPrivilege);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public List<FormPrivilege> saveFormPrivileges(List<FormPrivilege> formPrivileges) {
        ObjectMapper mapper = new ObjectMapper();
        List<FormPrivilege> incomingFormPrivileges =
                mapper.convertValue(formPrivileges, new TypeReference<List<FormPrivilege>>() {});
        List<FormPrivilege> resultList = new ArrayList<FormPrivilege>();
        List<FormPrivilege> oldPrivilegeList = new ArrayList<FormPrivilege>();
        List<FormPrivilege> privilegesList = new ArrayList<FormPrivilege>();
        for(FormPrivilege temp : incomingFormPrivileges) {
            FormPrivilege tempPrivilege = new FormPrivilege();
            Optional.ofNullable(temp.getFormId()).ifPresent(x -> { tempPrivilege.setFormId((Integer) x); });
            Optional.ofNullable(temp.getEditable()).ifPresent(x -> { tempPrivilege.setEditable((Boolean) x); });
            Optional.ofNullable(temp.getViewable()).ifPresent(x -> { tempPrivilege.setViewable((Boolean) x); });
            Optional.ofNullable(temp.getPrivilegeName()).ifPresent(x -> { tempPrivilege.setPrivilegeName((String) x); });
            Optional.ofNullable(temp.getFormVersion()).ifPresent(x -> { tempPrivilege.setFormVersion((String) x); });
            privilegesList.add(tempPrivilege);
        }
        if (privilegesList.size() == 1 && privilegesList.get(0).getPrivilegeName().equalsIgnoreCase("")) {
            if((privilegesList.get(0).getFormId() != null) && (privilegesList.get(0).getFormVersion() != null)) {
                deleteAllThePrivilegesFromDB(privilegesList.get(0).getFormId(), privilegesList.get(0).getFormVersion());
            }
        } else {
            if(privilegesList.size() > 0){
                oldPrivilegeList = getAllPrivilegesForForm(privilegesList.get(0).getFormId(), privilegesList.get(0).getFormVersion());
                if ((oldPrivilegeList != null) && !(oldPrivilegeList.isEmpty())) {
                    if (oldPrivilegeList.get(0).getFormId() == privilegesList.get(0).getFormId()) {
                        for (int i = 0; i < oldPrivilegeList.size(); i++) {
                            bahmniFormPrivilegeDao.deleteFormPrivilege(oldPrivilegeList.get(i));
                        }
                    }
                }
                if (privilegesList != null) {
                    for (int i = 0; i < privilegesList.size(); i++) {
                        resultList.add(saveFormPrivilege(privilegesList.get(i)));
                    }
                }
            }
        }
        return resultList;

    }
    @Transactional(readOnly = true)
    public FormPrivilege getFormPrivilege(String privilgeName , Integer formId) throws APIException {
        return bahmniFormPrivilegeDao.getFormPrivilege(privilgeName,formId);
    }
    public void deleteAllThePrivilegesFromDB( Integer formId , String formVersion){
        List<FormPrivilege> privilegeListToBeDeleted = getAllPrivilegesForForm(formId, formVersion);
        if ((privilegeListToBeDeleted != null) && !(privilegeListToBeDeleted.isEmpty())) {
            for (int i = 0; i < privilegeListToBeDeleted.size(); i++) {
                bahmniFormPrivilegeDao.deleteFormPrivilege(privilegeListToBeDeleted.get(i));
            }
        }
    }
    @Override
    public List<FormPrivilege> getAllPrivilegesForForm(Integer formId , String formVersion) {
        List<FormPrivilege> formPrivileges = bahmniFormPrivilegeDao.getAllPrivilegesForForm(formId , formVersion);
        return formPrivileges;

    }
    @Override
    public List<FormPrivilege> getFormPrivilegeGivenFormUuid(String formUuid, Integer formId) {
        List<FormPrivilege> formPrivileges = bahmniFormPrivilegeDao.getFormPrivilegeGivenFormUuid(formUuid,formId);
        return formPrivileges;

    }
    @Override
    public List<FormPrivilege>deleteAllPrivilegesForGivenFormId(Integer formId , String formVersion) {
        List<FormPrivilege> formPrivileges = bahmniFormPrivilegeDao.getAllPrivilegesForForm(formId , formVersion);
        for(int i = 0;i<formPrivileges.size();i++){
            bahmniFormPrivilegeDao.deleteFormPrivilege(formPrivileges.get(i));
        }
        return formPrivileges;
    }
    private boolean comparePrivileges(FormPrivilege originalFormPrivilege, FormPrivilege newPrivilege){
        if((originalFormPrivilege.getFormId() == newPrivilege.getFormId())
                && (originalFormPrivilege.getFormVersion() == newPrivilege.getFormVersion())
                && (originalFormPrivilege.getPrivilegeName() == newPrivilege.getPrivilegeName())){
            return true;
        }else{
            return false;
        }
    }

}
