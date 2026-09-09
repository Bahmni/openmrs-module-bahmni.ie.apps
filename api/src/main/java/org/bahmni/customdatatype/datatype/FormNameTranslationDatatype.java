/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.customdatatype.datatype;

import org.openmrs.customdatatype.SerializingCustomDatatype;

public class FormNameTranslationDatatype extends SerializingCustomDatatype<String> {

    @Override
    public String serialize(String typedValue) {
        return typedValue;
    }

    @Override
    public String deserialize(String serializedValue) {
        return serializedValue;
    }
}
