/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.model;

import java.util.ArrayList;
import java.util.Map;

public class FormFieldTranslations {

	private Map<String, ArrayList<String>> concepts;

	private Map<String, ArrayList<String>> labels;

	private String locale;

	public FormFieldTranslations(Map<String, ArrayList<String>> concepts, Map<String, ArrayList<String>> labels,
			String locale) {
		this.concepts = concepts;
		this.labels = labels;
		this.locale = locale;
	}

	public FormFieldTranslations() {
	}

	public Map<String, ArrayList<String>> getConcepts() {
		return concepts;
	}

	public void setConcepts(Map<String, ArrayList<String>> concepts) {
		this.concepts = concepts;
	}

	public Map<String, ArrayList<String>> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, ArrayList<String>> labels) {
		this.labels = labels;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
