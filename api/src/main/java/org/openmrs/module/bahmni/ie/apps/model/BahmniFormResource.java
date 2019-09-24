package org.openmrs.module.bahmni.ie.apps.model;

public class BahmniFormResource {

	private BahmniForm form;

	private String uuid;

	private String value;

	private String dataType;

	public BahmniForm getForm() {
		return form;
	}

	public void setForm(BahmniForm form) {
		this.form = form;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
