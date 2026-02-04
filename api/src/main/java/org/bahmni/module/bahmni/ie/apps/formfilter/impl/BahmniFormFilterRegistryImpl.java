package org.bahmni.module.bahmni.ie.apps.formfilter.impl;

import org.bahmni.module.bahmni.ie.apps.formfilter.BahmniFormFilter;
import org.bahmni.module.bahmni.ie.apps.formfilter.BahmniFormFilterRegistry;
import org.springframework.stereotype.Component;

@Component("bahmniFormFilterRegistry")
public class BahmniFormFilterRegistryImpl implements BahmniFormFilterRegistry {
    private BahmniFormFilter formFilter;

    @Override
    public BahmniFormFilter getFilter() {
        return formFilter;
    }

    @Override
    public void registerFilter(BahmniFormFilter filter) {
        this.formFilter = filter;
    }


}
