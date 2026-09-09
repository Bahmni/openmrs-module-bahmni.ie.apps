/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls;

import com.itextpdf.text.DocumentException;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field.IField;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.mapper.FieldMapper;
import org.bahmni.module.bahmni.ie.apps.util.pdf.BahmniPDFForm;
import org.json.JSONObject;

import java.io.IOException;

import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.CONCEPT;
import static org.bahmni.module.bahmni.ie.apps.util.json.impl.Constants.DATATYPE;

public class ObsControl implements IControl {
    @Override
    public void print(BahmniPDFForm bahmniPDFForm, JSONObject control) throws IOException, DocumentException {
        String datatype = (String) ((JSONObject) control.get(CONCEPT)).get(DATATYPE);
        IField iField = FieldMapper.stringToFieldMap.get(datatype);
        iField.addField(bahmniPDFForm, control);
    }
}
