package com.carwasher.testyanexkassa.vo.yandexkassa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancellationDetails {
    @SerializedName("type")
    @Expose
    private String party;
    @SerializedName("return_url")
    @Expose
    private String reason;



    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
