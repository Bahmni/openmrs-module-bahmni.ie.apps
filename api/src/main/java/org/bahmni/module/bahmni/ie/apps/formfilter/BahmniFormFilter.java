package org.bahmni.module.bahmni.ie.apps.formfilter;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;

import java.util.List;

public interface BahmniFormFilter {
    List<BahmniForm> filter(BahmniFormFilterParams bahmniFormFilterParams);
}
