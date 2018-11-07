package com.carwasher.testyanexkassa.auth.yandexkassa;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class YandexKassaBasicAuthenticator implements Authenticator {
    private String authToken;

    protected YandexKassaBasicAuthenticator(){}

    public YandexKassaBasicAuthenticator(String token){
        authToken = token;
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Request request = response.request();
        if (request.header("Authorization") != null)
            // Логин и пароль неверны
            return null;
        return request.newBuilder()
                .header("Authorization", authToken)
                .build();
    }
}
