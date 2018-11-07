package com.carwasher.testyanexkassa.service;

import android.support.annotation.NonNull;

import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.PaymentMethodType;

public interface IYandexKassaMSDKResultListener extends Checkout.ResultCallback{
    void onResult(@NonNull String paymentToken, @NonNull PaymentMethodType type);
}
