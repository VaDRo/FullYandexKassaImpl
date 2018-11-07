package com.carwasher.testyanexkassa.vo.yandexkassa;

import com.carwasher.testyanexkassa.enums.PaymentStatuses;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import ru.yandex.money.android.sdk.PaymentMethodType;

public class YandexKassaPayment {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("paid")
    @Expose
    private Boolean paid;
    @SerializedName("amount")
    @Expose
    private Amount amount;
    @SerializedName("confirmation")
    @Expose
    private Confirmation confirmation;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("cancellation_details")
    @Expose
    private CancellationDetails cancellationDetails;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("payment_method")
    @Expose
    private PaymentMethod paymentMethod;
    @SerializedName("payment_token")
    @Expose
    private String paymentToken;
    @SerializedName("payment_method_data")
    @Expose
    private PaymentMethod paymentMethodData;
    @SerializedName("capture")
    @Expose
    private boolean capture;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("parameter")
    @Expose
    private String parameter;
    /*@SerializedName("test")
    @Expose
    private Boolean test;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();*/

    private String errorDescription;

/*
    public YandexKassaPayment(String amount, String currency, String description, String returnUrl, String paymentToken){
        this.amount = new Amount(amount, currency);
        this.description = description;
        this.paymentMethodData = new PaymentMethod("bank_card");
        this.confirmation =  new Confirmation("redirect", returnUrl);
        this.paymentToken = paymentToken;
    }
*/

    public YandexKassaPayment(String amount, String currency, String description, String returnUrl, PaymentMethodType paymentMethodType, String paymentToken, String orderId){
        this.amount = new Amount(amount, currency);
        //this.description = description;
        //this.paymentMethodData = new PaymentMethod(paymentMethodType.toString().toLowerCase(), orderId);
        this.confirmation =  new Confirmation("redirect", returnUrl);
        this.paymentToken = paymentToken;
        this.capture = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public boolean getCapture() {
        return capture;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    public CancellationDetails getCancellationDetails() {
        return cancellationDetails;
    }

    public void setCancellationDetails(CancellationDetails details) {
        this.cancellationDetails = cancellationDetails;
    }

    public String getResultDescription(){
        String res = (status != null && "canceled".equals(status)) ? "payment is canceled" : ("payment status is " + ((status != null) ? status : null));
        res += ", payment is " + ((paid) ? "paid" : "unpaid");
        return res;
    }

    public String getErrorDescription(){
        return errorDescription;
    }

    public PaymentStatuses getPaymentStatus(){
        if (status == null)
            return PaymentStatuses.unknown;
        if ("canceled".equals(status))
            return PaymentStatuses.canceled;
        if ("succeeded".equals(status))
            return PaymentStatuses.success;
        if ("pending".equals(status))
            return PaymentStatuses.processing;
        if ("waiting_for_capture".equals(status))
            return PaymentStatuses.wait_accept;
        return PaymentStatuses.unknown;
    }

    public void setErrorDescription(String text){
        errorDescription = text;
    }

    public YandexKassaPayment updateFromWebAPIResponse(YandexKassaPayment result){
        if (result.type != null && "error".equals(result.type)){
            setErrorDescription("code: " + code + ", description: " + description + ", parameter: " + parameter);
        }else{
            setErrorDescription("");
        }

        this.status = result.status;
        this.paid = result.paid;
        this.amount = result.amount;
        this.createdAt = result.createdAt;
        this.cancellationDetails = result.cancellationDetails;
        this.paymentMethod = result.paymentMethod;
        this.id = result.id;
        this.confirmation = result.confirmation;

        return this;
    }

}
