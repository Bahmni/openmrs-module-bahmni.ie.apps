package org.openmrs.module.bahmni.ie.apps.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.module.bahmni.ie.apps.model.BahmniFormResource;
import org.openmrs.module.bahmni.ie.apps.MotherForm;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.FormResource;

public class BahmniFormMapperTest {

	@Test
	public void shouldMapFormResourceToBahmniFormResourceObject() {
		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
		FormResource formResource = MotherForm.createFormResource(1, "value", "FormResourceUuid", form);
		BahmniFormResource bahmniFormResource = new BahmniFormMapper().map(formResource);

		Assert.assertNotNull(bahmniFormResource);
		Assert.assertEquals("FormResourceUuid", bahmniFormResource.getUuid());

		Assert.assertNotNull(bahmniFormResource.getForm());
		Assert.assertEquals("FormName", bahmniFormResource.getForm().getName());
		Assert.assertEquals("FormUuid", bahmniFormResource.getForm().getUuid());
		Assert.assertEquals("FormVersion", bahmniFormResource.getForm().getVersion());
		Assert.assertTrue(bahmniFormResource.getForm().isPublished());
	}

	@Test
	public void shouldMapFormToBahmniFormObject() {
		Form form = MotherForm.createForm("FormName", "FormUuid", "FormVersion", true);
		BahmniForm bahmniForm = new BahmniFormMapper().map(form);
		Assert.assertNotNull(bahmniForm);
		Assert.assertEquals("FormName", bahmniForm.getName());
		Assert.assertEquals("FormUuid", bahmniForm.getUuid());
		Assert.assertEquals("FormVersion", bahmniForm.getVersion());
		Assert.assertTrue(bahmniForm.isPublished());
	}
}
