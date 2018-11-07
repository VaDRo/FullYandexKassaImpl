package com.carwasher.testyanexkassa.service;

import com.carwasher.testyanexkassa.auth.yandexkassa.YandexKassaBasicAuthenticator;
import com.carwasher.testyanexkassa.vo.yandexkassa.YandexKassaPayment;
import com.carwasher.testyanexkassa.webapi.interfaces.yandexkassa.IYandexKassaAPIService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class YandexKassaAPIService {
    private Retrofit retrofit;
    private IYandexKassaAPIService kassaServerAPI;
    private String baseUrl ="https://payment.yandex.net/api/v3/";

    private static YandexKassaAPIService _instance = null;
    private YandexKassaAPIService(){}

    public static YandexKassaAPIService getInstance(){
        if (_instance == null){
            _instance = new YandexKassaAPIService();
            _instance.initAPI();
        }

        return _instance;
    }

    private void initAPI() {
        //https://habr.com/post/314028/
        //https://kassa.yandex.ru/docs/checkout-api/?shell#ispol-zowanie-api
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        ConfigurationService service = ConfigurationService.getInstance();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .authenticator(new YandexKassaBasicAuthenticator(Credentials.basic(service.getYandexKassaShopId(), service.getYandexKassaWebAPIPrivateKey())))
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        kassaServerAPI = retrofit.create(IYandexKassaAPIService.class);
    }

    public void checkPayment(String idempotenceKey, YandexKassaPayment payment, final IYandexKassaPaymentResultListener listener) {
        kassaServerAPI.checkPayment(idempotenceKey, payment)
                .enqueue(new Callback<YandexKassaPayment>() {
                    @Override
                    public void onResponse(Call<YandexKassaPayment> call, Response<YandexKassaPayment> response) {
                        listener.onCheckPaymentResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<YandexKassaPayment> call, Throwable t) {
                        listener.onCheckPaymentFailure(call, t);
                    }
                });
    }
}
