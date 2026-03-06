/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.mapper;

import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.field.*;

import java.util.HashMap;
import java.util.Map;

public class FieldMapper {
    public static final Map<String, IField> stringToFieldMap = new HashMap() {{
        put("Text", new TextField());
        put("Numeric", new NumericField());
        put("Date", new DateField());
        put("Datetime", new DatetimeField());
        put("Boolean", new BooleanField());
        put("Coded", new CodedField());
    }};
}
