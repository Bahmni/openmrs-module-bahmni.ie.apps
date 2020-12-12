package org.bahmni.module.bahmni.ie.apps;

import org.bahmni.module.bahmni.ie.apps.model.FormPrivilege;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormResource;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;

public class MotherForm {

	public static FormResource createFormResource(Integer id, String value, String uuid, Form form) {
		return new FormResource() {{
			setId(id);
			setValue(value);
			setUuid(uuid);
			setForm(form);
		}};
	}

	public static Form createForm(String name, String uuid, String version, boolean isPublished) {
		return new Form() {{
			setName(name);
			setUuid(uuid);
			setVersion(version);
			setPublished(isPublished);
		}};
	}

	public static BahmniForm createBahmniForm(String name, String uuid, String version, boolean isPublished) {
		return new BahmniForm() {{
			setName(name);
			setUuid(uuid);
			setVersion(version);
			setPublished(isPublished);
		}};
	}

	public static BahmniForm createBahmniForm(String formName, String uuid) {
		return new BahmniForm() {{
			setName(formName);
			setUuid(uuid);
		}};
	}

	public static BahmniFormResource createBahmniFormResource(String uuid, String valueReference, BahmniForm bahmniForm) {
		return new BahmniFormResource() {{
			setUuid(uuid);
			setValue(valueReference);
			setForm(bahmniForm);
		}};
	}
	public static FormPrivilege createFormPrivilege(Integer formId, String privilegeName, Boolean editable, Boolean viewable , String formVersion){
		return new FormPrivilege(){{
			setFormId(formId);
			setPrivilegeName(privilegeName);
			setEditable(editable);
			setViewable(viewable);
			setFormVersion(formVersion);
		}};
	}
}
