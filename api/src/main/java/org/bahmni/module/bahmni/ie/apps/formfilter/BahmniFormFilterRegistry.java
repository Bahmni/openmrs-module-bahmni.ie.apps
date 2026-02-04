package org.bahmni.module.bahmni.ie.apps.formfilter;

public interface BahmniFormFilterRegistry {
    BahmniFormFilter getFilter();

    void registerFilter(BahmniFormFilter bahmniFormFilter);
}
