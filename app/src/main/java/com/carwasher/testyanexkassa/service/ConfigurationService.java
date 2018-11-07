package com.carwasher.testyanexkassa.service;

public class ConfigurationService {
    private static ConfigurationService _instance;

    private String YandexKassaShopId = "YOUR_SHOP_ID";
    //Mobile SDK secret key - key for Yandex.Kassa MSDK
    private String YandexKassaMSDKPrivateKey = "YOUR_MOBILE_SDK_SECRET_KEY";
    //API Secret Key - key for accessing Yandex.Kassa Web API
    private String YandexKassaPrivateKey = "YOUR_WEB_API_SECRET_KEY";
    private int RetryInterval = 10000;     //Retry interval, milliseconds
    private boolean isTestMode = false;

    private ConfigurationService() {    }

    public static ConfigurationService getInstance() {
        if (_instance == null)
            _instance = new ConfigurationService();
        return _instance;
    }

    public boolean isTestMode(){
        return isTestMode;
    }

    public String getYandexKassaMSDKPrivateKey(){
        return YandexKassaMSDKPrivateKey;
    }

    public String getYandexKassaWebAPIPrivateKey(){
        return YandexKassaPrivateKey;
    }

    public String getYandexKassaShopId() {
        return YandexKassaShopId;
    }

    public int getRetryInterval() { return RetryInterval; }

}
