package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CkanLicense {

    private String status;
    private String maintainer;
    @JsonProperty("od_conformance")
    private String odConformance;
    private String family;
    @JsonProperty("osd_conformance")
    private String osdConformance;
    @JsonProperty("domain_data")
    private String domainData;
    private String title;
    private String url;
    @JsonProperty("is_generic")
    private String isGeneric;
    @JsonProperty("is_okd_compliant")
    private boolean isOkdCompliant;
    @JsonProperty("is_osi_compliant")
    private boolean isOsiCompliant;
    @JsonProperty("domain_content")
    private String domainContent;
    @JsonProperty("domain_software")
    private String domainSoftware;
    private String id;


    public CkanLicense() {
    }

    public CkanLicense(String status, String maintainer, String odConformance, String family, String osdConformance, String domainData, String title, String url, String isGeneric, boolean isOkdCompliant, boolean isOsiCompliant, String domainContent, String domainSoftware, String id) {
        this.status = status;
        this.maintainer = maintainer;
        this.odConformance = odConformance;
        this.family = family;
        this.osdConformance = osdConformance;
        this.domainData = domainData;
        this.title = title;
        this.url = url;
        this.isGeneric = isGeneric;
        this.isOkdCompliant = isOkdCompliant;
        this.isOsiCompliant = isOsiCompliant;
        this.domainContent = domainContent;
        this.domainSoftware = domainSoftware;
        this.id = id;
    }


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMaintainer() {
        return maintainer;
    }
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }
    public String getOdConformance() {
        return odConformance;
    }
    public void setOdConformance(String odConformance) {
        this.odConformance = odConformance;
    }
    public String getFamily() {
        return family;
    }
    public void setFamily(String family) {
        this.family = family;
    }
    public String getOsdConformance() {
        return osdConformance;
    }
    public void setOsdConformance(String osdConformance) {
        this.osdConformance = osdConformance;
    }
    public String getDomainData() {
        return domainData;
    }
    public void setDomainData(String domainData) {
        this.domainData = domainData;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getIsGeneric() {
        return isGeneric;
    }
    public void setIsGeneric(String isGeneric) {
        this.isGeneric = isGeneric;
    }
    public boolean isOkdCompliant() {
        return isOkdCompliant;
    }
    public void setOkdCompliant(boolean okdCompliant) {
        isOkdCompliant = okdCompliant;
    }
    public boolean isOsiCompliant() {
        return isOsiCompliant;
    }
    public void setOsiCompliant(boolean osiCompliant) {
        isOsiCompliant = osiCompliant;
    }
    public String getDomainContent() {
        return domainContent;
    }
    public void setDomainContent(String domainContent) {
        this.domainContent = domainContent;
    }
    public String getDomainSoftware() {
        return domainSoftware;
    }
    public void setDomainSoftware(String domainSoftware) {
        this.domainSoftware = domainSoftware;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
