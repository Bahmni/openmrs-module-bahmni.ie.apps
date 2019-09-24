package org.openmrs.module.bahmni.ie.apps.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.bahmni.ie.apps.model.BahmniForm;
import org.openmrs.module.bahmni.ie.apps.model.BahmniFormResource;

import java.util.List;

public interface BahmniFormService extends OpenmrsService {

	BahmniFormResource saveFormResource(BahmniFormResource bahmniFormResource);

	BahmniForm publish(String formUuid);

	List<BahmniForm> getAllLatestPublishedForms(boolean includeRetired, String encounterUuid);

	List<BahmniForm> getAllForms();
}
