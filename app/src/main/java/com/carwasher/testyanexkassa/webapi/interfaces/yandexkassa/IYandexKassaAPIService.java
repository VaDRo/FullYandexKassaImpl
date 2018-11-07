package com.carwasher.testyanexkassa.webapi.interfaces.yandexkassa;

import com.carwasher.testyanexkassa.vo.yandexkassa.YandexKassaPayment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IYandexKassaAPIService {
    //sample: https://payment.yandex.net/api/v3/payments \
    //  -X POST \
    //  -u <Идентификатор магазина>:<Секретный ключ> \
    //  -H 'Idempotence-Key: <Ключ идемпотентности>' \
    //  -H 'Content-Type: application/json' \
    //  -d '{
    //        "amount": {
    //          "value": "2.00",
    //          "currency": "RUB"
    //        },
    //        "payment_method_data": {
    //          "type": "bank_card"
    //        },
    //        "confirmation": {
    //          "type": "redirect",
    //          "return_url": "https://www.merchant-website.com/return_url"
    //        },
    //        "description": "Заказ №72"
    //      }'
    @POST("payments")
    @Headers("Content-Type: application/json")
    public Call<YandexKassaPayment> checkPayment(@Header("Idempotence-Key") String idempotenceKey, @Body YandexKassaPayment payment);

}
