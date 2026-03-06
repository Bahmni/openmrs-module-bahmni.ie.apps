/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.mapper;

import org.bahmni.module.bahmni.ie.apps.MotherForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BahmniFormMapperTest {

    @Test
    public void shouldMapFormResourceToBahmniFormResourceObject() {
        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
        FormResource formResource = MotherForm.createFormResource(1, "value", "FormResourceUuid", form);
        BahmniFormResource bahmniFormResource = new BahmniFormMapper().map(formResource);

        assertNotNull(bahmniFormResource);
        assertEquals("FormResourceUuid", bahmniFormResource.getUuid());

        assertNotNull(bahmniFormResource.getForm());
        assertEquals("FormName", bahmniFormResource.getForm().getName());
        assertEquals("FormUuid", bahmniFormResource.getForm().getUuid());
        assertEquals("FormVersion", bahmniFormResource.getForm().getVersion());
        assertTrue(bahmniFormResource.getForm().isPublished());
    }

    @Test
    public void shouldMapFormToBahmniFormObject() {
        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
        BahmniForm bahmniForm = new BahmniFormMapper().map(form);
        assertNotNull(bahmniForm);
        assertEquals("FormName", bahmniForm.getName());
        assertEquals("FormUuid", bahmniForm.getUuid());
        assertEquals("FormVersion", bahmniForm.getVersion());
        assertTrue(bahmniForm.isPublished());
    }

    @Test
    public void shouldMapFormToBahmniFormWhenResourcesArePresent() {
        Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
        List<BahmniFormResource> resources = new ArrayList<>();
        resources.add(new BahmniFormResource());
        BahmniForm bahmniForm = new BahmniFormMapper().map(form, resources);
        assertNotNull(bahmniForm);
        assertEquals("FormName", bahmniForm.getName());
        assertEquals("FormUuid", bahmniForm.getUuid());
        assertEquals("FormVersion", bahmniForm.getVersion());
        assertEquals(resources, bahmniForm.getResources());
        assertTrue(bahmniForm.isPublished());
    }

    @Test
    public void shouldReturnNullWhenFormIsNull() {
        BahmniForm bahmniForm = new BahmniFormMapper().map(null, null);
        assertNull(bahmniForm);
    }

    @Test
    public void shouldMapFormResourceListToBahmniFormResourceList() {
        Collection<FormResource> formResources = new ArrayList<>();
        FormResource formResourceOne = new FormResource();
        formResourceOne.setValue("Form Resource one");
        FormResource formResourceTwo = new FormResource();
        formResourceTwo.setValue("Form Resource two");
        FormResource formResourceThree = new FormResource();
        formResourceThree.setValue("Form Resource three");
        formResources.add(formResourceOne);
        formResources.add(formResourceTwo);
        formResources.add(formResourceThree);
        List<BahmniFormResource> bahmniFormResources = new BahmniFormMapper().mapResources(formResources);
        assertNotNull(bahmniFormResources);
        assertEquals(3, bahmniFormResources.size());
        assertEquals("Form Resource one", bahmniFormResources.get(0).getValue());
        assertEquals("Form Resource two", bahmniFormResources.get(1).getValue());
        assertEquals("Form Resource three", bahmniFormResources.get(2).getValue());
    }
}
