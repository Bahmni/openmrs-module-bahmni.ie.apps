package org.bahmni.module.bahmni.ie.apps.util.json.impl.controlparser;

import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.IControl;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.LabelControl;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.ObsControl;
import org.bahmni.module.bahmni.ie.apps.util.json.impl.controls.SectionControl;

import java.util.HashMap;
import java.util.Map;

public class ControlParser {
    public static final Map<String, IControl> stringToControlMap = new HashMap() {{
        put("label", new LabelControl());
        put("obsControl", new ObsControl());
        put("section", new SectionControl());
    }};
}

