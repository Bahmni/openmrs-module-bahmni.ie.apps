package org.bahmni.module.bahmni.ie.apps.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.customdatatype.CustomValueDescriptor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

public class FormPrivilege extends BaseOpenmrsMetadata {

    private Integer formId;
    private String privilegeName;
    private Boolean editable;
    private Boolean viewable;

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVersion) {
        this.formVersion = formVersion;
    }

    private String formVersion;
    private Integer form_privilege_id;

    public FormPrivilege() {
    }
    public FormPrivilege(Integer formId, String privilegeName, Boolean editable, Boolean viewable , String formVersion) {

        this.formId = formId;
        this.privilegeName = privilegeName;
        this.editable = editable;
        this.viewable = viewable;
        this.formVersion = formVersion;

    }
    public Integer getForm_privilege_id() {
        return form_privilege_id;
    }

    public void setForm_privilege_id(Integer form_privilege_id) {
        this.form_privilege_id = form_privilege_id;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }


    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getViewable() {
        return viewable;
    }

    public void setViewable(Boolean viewable) {
        this.viewable = viewable;
    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer integer) {

    }
}