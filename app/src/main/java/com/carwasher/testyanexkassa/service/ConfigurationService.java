package com.carwasher.testyanexkassa.service;

public class ConfigurationService {
    private static ConfigurationService _instance;

    private final String MyBaseUrl = "https://washercar.ru";
    private final String YandexKassaShopId = "Your shop ID";
    //Mobile SDK secret key - key for Yandex.Kassa MSDK
    private final String YandexKassaMSDKPrivateKey = "Your MobileSDK key";
    //API Secret Key - key for accessing Yandex.Kassa Web API
    private final String YandexKassaPrivateKey = "Your Yandex.Kassa WebAPI key";
    private final String MyShopName = "Тестовый магазин";
    private final String MyShopDescription = "Это просто тестовый магазин";

    public String getMyShopName() {
        return MyShopName;
    }

    public String getMyShopDescription() {
        return MyShopDescription;
    }

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

    public String getMyBaseUrl(){
        return MyBaseUrl;
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
