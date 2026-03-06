/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.mapper;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.openmrs.Form;
import org.openmrs.FormResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BahmniFormMapper   {
    public BahmniFormResource map(FormResource formResource) {
        BahmniFormResource bahmniFormResource = new BahmniFormResource();
        bahmniFormResource.setValue(formResource.getValue().toString());
        bahmniFormResource.setUuid(formResource.getUuid());
        bahmniFormResource.setForm(map(formResource.getForm()));
        bahmniFormResource.setDataType(formResource.getDatatypeClassname());
        return bahmniFormResource;
    }

    public BahmniForm map(Form form) {
        if (form == null) return null;
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setName(form.getName());
        bahmniForm.setUuid(form.getUuid());
        bahmniForm.setVersion(form.getVersion());
        bahmniForm.setPublished(form.getPublished());
        bahmniForm.setId(form.getFormId());
        return bahmniForm;
    }

    public BahmniForm map(Form form, List<BahmniFormResource> resources) {
        if (form == null) return null;
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setId(form.getId());
        bahmniForm.setName(form.getName());
        bahmniForm.setUuid(form.getUuid());
        bahmniForm.setVersion(form.getVersion());
        bahmniForm.setPublished(form.getPublished());
        bahmniForm.setResources(resources);
        return bahmniForm;
    }

    public List<BahmniFormResource> mapResources(Collection<FormResource> formResourcesForForm) {
        if (formResourcesForForm == null) return null;
        List<BahmniFormResource> formResources = new ArrayList<>();
        for (FormResource formResource : formResourcesForForm) {
            BahmniFormResource bahmniFormResource = new BahmniFormResource();
            bahmniFormResource.setDataType(formResource.getDatatypeClassname());
            bahmniFormResource.setUuid(formResource.getUuid());
            bahmniFormResource.setValue((String) formResource.getValue());
            formResources.add(bahmniFormResource);
        }
        return formResources;
    }


}
