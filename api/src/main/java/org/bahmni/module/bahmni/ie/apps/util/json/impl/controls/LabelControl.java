/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.VALUE;

public class LabelControl implements IControl {

    @Override
    public void print(BahmniPDFForm bahmniPDFForm, JSONObject control) {
        bahmniPDFForm.addLabel((String) control.get(VALUE));
    }
}
