package com.carwasher.testyanexkassa.vo.yandexkassa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethod {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("saved")
    @Expose
    private Boolean saved;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public PaymentMethod(String type){
        this.type = type;
    }

    public PaymentMethod(String type, String id){
        this.type = type;
        this.id = id;
        saved = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
