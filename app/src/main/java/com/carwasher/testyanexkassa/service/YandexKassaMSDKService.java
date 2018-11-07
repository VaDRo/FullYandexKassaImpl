package com.carwasher.testyanexkassa.service;

import android.content.Context;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;

import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.Configuration;
import ru.yandex.money.android.sdk.PaymentMethodType;
import ru.yandex.money.android.sdk.ShopParameters;

public class YandexKassaMSDKService {
    private static YandexKassaMSDKService _instance;
    private YandexKassaMSDKService(){}

    public static YandexKassaMSDKService getInstance() {
        if (_instance == null)
            _instance = new YandexKassaMSDKService();

        return _instance;
    }

    public void prepareCheckout(Context context, BigDecimal amount, String currency, String orderId) {
        ConfigurationService service = ConfigurationService.getInstance();

        if (service.isTestMode()){
            Checkout.configureTestMode(
                    new Configuration(true, false, false, 1, true, true)
            );
        }

        ShopParameters parameters = new ShopParameters(
                "<Название магазина>",
                "<Описание магазина>",
                service.getYandexKassaMSDKPrivateKey(),
                //new HashSet<PaymentMethodType>(Arrays.asList(PaymentMethodType.BANK_CARD)), // разрешенные способы оплаты (если передать пустое множество, покупатель увидит все способы)
                Collections.singleton(PaymentMethodType.BANK_CARD), // разрешенные способы оплаты (если передать пустое множество, покупатель увидит все способы)
                false, // включить Google Pay (нужно согласовать с менеджером Яндекс.Кассы)
                service.getYandexKassaShopId(), // идентификатор магазина в личном кабинете (опционально, нужен для Google Pay)
                null, // идентификатор шлюза (опционально, нужен, если вы используете разные шлюзы)
                true // показывает и скрывает логотип Яндекс.Кассы
        );
        Checkout.tokenize(
                context,
                new Amount(amount, Currency.getInstance(currency)),         //Currency.getInstance("RUB"))
                parameters
        );
    }
}

