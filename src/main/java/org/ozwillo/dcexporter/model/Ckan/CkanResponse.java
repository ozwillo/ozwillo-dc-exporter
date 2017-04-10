package org.ozwillo.dcexporter.model.Ckan;

public class CkanResponse {

    private String help;
    private boolean success;


    public CkanResponse() {
    }

    public CkanResponse(String help, boolean success) {
        this.help = help;
        this.success = success;
    }


    public String getHelp() {
        return help;
    }
    public void setHelp(String help) {
        this.help = help;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
}