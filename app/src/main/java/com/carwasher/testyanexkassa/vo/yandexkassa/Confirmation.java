package com.carwasher.testyanexkassa.vo.yandexkassa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Confirmation {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("return_url")
    @Expose
    private String returnUrl;
    @SerializedName("confirmation_url")
    @Expose
    private String confirmationUrl;
    //private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Confirmation(String type, String returnUrl){
        this.type = type;
        this.returnUrl = returnUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getConfirmationUrl() {
        return confirmationUrl;
    }

    public void setConfirmationUrl(String confirmationUrl) {
        this.confirmationUrl = confirmationUrl;
    }

    /*public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/
}
