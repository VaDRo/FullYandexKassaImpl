package com.carwasher.testyanexkassa.service;

import com.carwasher.testyanexkassa.vo.yandexkassa.YandexKassaPayment;

import retrofit2.Call;
import retrofit2.Response;

public interface IYandexKassaPaymentResultListener {
    void onCheckPaymentResponse(Call<YandexKassaPayment> call, Response<YandexKassaPayment> response);

    void onCheckPaymentFailure(Call<YandexKassaPayment> call, Throwable t);

}
