/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.model;

import java.util.List;

public class BahmniFormData {
    private BahmniForm formJson;
    private List<FormTranslation> translations;

    public BahmniForm getFormJson() {
        return formJson;
    }

    public void setFormJson(BahmniForm formJson) {
        this.formJson = formJson;
    }

    public List<FormTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<FormTranslation> translations) {
        this.translations = translations;
    }
}
