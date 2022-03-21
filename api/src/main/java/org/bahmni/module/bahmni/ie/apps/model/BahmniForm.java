package org.bahmni.module.bahmni.ie.apps.model;

import java.util.List;

public class BahmniForm {
    private String name;
    private String uuid;
    private String version;
    private boolean published;
    private Integer id;
    private List<BahmniFormResource> resources;

    public List<FormPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<FormPrivilege> privileges) {
        this.privileges = privileges;
    }

    private List<FormPrivilege> privileges;

    private String nameTranslation;

    public BahmniForm(){}

    public BahmniForm(String name, String uuid,  String version, boolean published, Integer id,String nameTranslation){
        this.name=name;
        this.uuid= uuid;
        this.version=version;
        this.published=published;
        this.id=id;
        this.nameTranslation = nameTranslation;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<BahmniFormResource> getResources() {
        return resources;
    }

    public void setResources(List<BahmniFormResource> resources) {
        this.resources = resources;
    }

    public String getNameTranslation() {
        return nameTranslation;
    }

    public void setNameTranslation(String nameTranslation) {
        this.nameTranslation = nameTranslation;
    }
}
