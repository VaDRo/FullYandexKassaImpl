package com.carwasher.testyanexkassa.vo.yandexkassa;

import java.util.HashMap;
import java.util.Map;

public class Amount {
    private String value;
    private String currency;
    //private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Amount(String value, String currency){
        this.value = value;
        this.currency = currency.toUpperCase();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /*public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

}
