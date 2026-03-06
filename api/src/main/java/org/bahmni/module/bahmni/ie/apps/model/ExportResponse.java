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

public class ExportResponse {
    private List<BahmniFormData> bahmniFormDataList;
    private List<String> errorFormList;

    public ExportResponse(List<BahmniFormData> bahmniFormDataList, List<String> errorFormList) {
        this.bahmniFormDataList = bahmniFormDataList;
        this.errorFormList = errorFormList;
    }

    public List<BahmniFormData> getBahmniFormDataList() {
        return bahmniFormDataList;
    }

    public void setBahmniFormDataList(List<BahmniFormData> bahmniFormDataList) {
        this.bahmniFormDataList = bahmniFormDataList;
    }

    public List<String> getErrorFormList() {
        return errorFormList;
    }

    public void setErrorFormList(List<String> errorFormList) {
        this.errorFormList = errorFormList;
    }
}
