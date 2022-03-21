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
