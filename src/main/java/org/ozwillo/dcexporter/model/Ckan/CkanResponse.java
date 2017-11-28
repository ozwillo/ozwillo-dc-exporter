package org.ozwillo.dcexporter.model.Ckan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkanResponse {

    private String help;
    private boolean success;
    private Error error;


    public CkanResponse() {
    }

    public CkanResponse(String help, boolean success, Error error) {
        this.help = help;
        this.success = success;
        this.error = error;
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
    public Error getError() {
        return error;
    }
    public void setError(Error error) {
        this.error = error;
    }

    public class Error {
        private String message;
        private String __type;

        public Error(String message, String __type) {
            this.message = message;
            this.__type = __type;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String get__type() {
            return __type;
        }
        public void set__type(String __type) {
            this.__type = __type;
        }
    }
}