package org.bahmni.module.bahmni.ie.apps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BahmniFormSearchParams {
    private String encounterUuid;
    private String episodeUuid;
    private boolean includeRetired = false;

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public String getEpisodeUuid() {
        return episodeUuid;
    }

    public void setEpisodeUuid(String episodeUuid) {
        this.episodeUuid = episodeUuid;
    }

    public boolean isIncludeRetired() {
        return includeRetired;
    }

    public void setIncludeRetired(boolean includeRetired) {
        this.includeRetired = includeRetired;
    }
}
