package org.bahmni.module.bahmni.ie.apps.formfilter;

import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.bahmni.module.bahmni.ie.apps.model.BahmniFormSearchParams;

import java.util.List;

public class BahmniFormFilterParams extends BahmniFormSearchParams {
    private List<BahmniForm> latestPublishedForms;

    public void setLatestPublishedForms(List<BahmniForm> latestPublishedForms) {
        this.latestPublishedForms = latestPublishedForms;
    }

    public List<BahmniForm> getLatestPublishedForms() {
        return latestPublishedForms;
    }
}
