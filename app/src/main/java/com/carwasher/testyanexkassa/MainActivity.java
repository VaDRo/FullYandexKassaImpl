package com.carwasher.testyanexkassa;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.carwasher.testyanexkassa.enums.Currencies;
import com.carwasher.testyanexkassa.enums.PaymentStatuses;
import com.carwasher.testyanexkassa.service.ConfigurationService;
import com.carwasher.testyanexkassa.service.IYandexKassaMSDKResultListener;
import com.carwasher.testyanexkassa.service.IYandexKassaPaymentResultListener;
import com.carwasher.testyanexkassa.service.YandexKassaAPIService;
import com.carwasher.testyanexkassa.service.YandexKassaMSDKService;
import com.carwasher.testyanexkassa.vo.yandexkassa.Confirmation;
import com.carwasher.testyanexkassa.vo.yandexkassa.YandexKassaPayment;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.PaymentMethodType;

public class MainActivity extends AppCompatActivity implements IYandexKassaMSDKResultListener, IYandexKassaPaymentResultListener {

    private AppCompatTextView mResult;
    private AppCompatTextView mResultError;
    private AppCompatTextView mRetryCntr;
    private AppCompatButton StopRefreshBtn;
    //private PaymentResult mPaymentResult;
    private Timer mTimer;
    private CheckPaymentResultTask checkPaymentResultTask;
    private boolean mAutoRefresh = false;
    private MainActivity thisActivity;
    private int mCounter = 0;
    private AppCompatButton mRunmeBtn;
    private LinearLayout mLoadingLayout;
    private Spinner mCurrencySpinner;
    private AppCompatEditText mPaymentAmount;

    private String mCurrentPaymentToken = null;
    private PaymentMethodType mCurrentPaymentMethod = null;
    private YandexKassaPayment mCurrentPayment;
    private String orderId;
    private String mCurrency;
    private String mAmount2pay;
    private boolean mFirstCheck = true;
    private boolean m3DSActivityShown = false;

    private final int ACTIVITY_3D_SECURE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Checkout.attach(this.getSupportFragmentManager());
        Checkout.setResultCallback(this);

        initUI();
        thisActivity = this;
    }

    @Override
    protected void onDestroy() {
        Checkout.detach();
        if (mTimer != null) {
            try{
                mTimer.cancel();
            }catch (Exception e){}
            mTimer = null;
        }
        super.onDestroy();
    }

    private void initUI(){
        mResult = findViewById(R.id.result);
        mResultError = findViewById(R.id.resultError);
        mRetryCntr = findViewById(R.id.retryCntr);
        mLoadingLayout = findViewById(R.id.loadingPanel);
        mPaymentAmount = findViewById(R.id.paymentAmount);

        mRunmeBtn = findViewById(R.id.simplePay);
        mRunmeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSimplePayButtonClicked();
            }
        });

        StopRefreshBtn = findViewById(R.id.stopRefresh);
        StopRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopRefreshButtonClicked();
            }
        });

        mCurrencySpinner = findViewById(R.id.currencySpinner);
        Currencies[] items = Currencies.values();
        ArrayAdapter<Currencies> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mCurrencySpinner.setAdapter(adapter);
        mCurrencySpinner.setSelection(Arrays.asList(items).indexOf(Currencies.RUB));

    }

    //Create payment token, payment is not sending anywhere Just token
    private void onSimplePayButtonClicked(){
        mLoadingLayout.setVisibility(View.VISIBLE);
        mRunmeBtn.setEnabled(false);
        mCurrency = mCurrencySpinner.getSelectedItem().toString();
        mAmount2pay = mPaymentAmount.getText().toString();
        orderId = String.valueOf(Math.round(Math.random()*10000)+10000);
        BigDecimal amount;
        try{
            amount = new BigDecimal(mAmount2pay);
        }catch(Exception e){
            //user specified non-double value
            mResultError.setText("Exception: specified non-currency value");
            return;
        }
        YandexKassaMSDKService.getInstance().prepareCheckout(this, amount, mCurrency, orderId);

    }

    //Result of process of tokenization
    //If everything is ok -> send payment to Yandex.Kassa web API
    @Override
    public void onResult(@NonNull String paymentToken, @NonNull PaymentMethodType type) {
        mCurrentPaymentToken = paymentToken;
        mCurrentPaymentMethod = type;
        mAutoRefresh = true;
        //Call Yandex.Kassa to process current payment
        mCurrentPayment = new YandexKassaPayment(mAmount2pay, mCurrency,"Test payment",
                ConfigurationService.getInstance().getMyBaseUrl(), mCurrentPaymentMethod, mCurrentPaymentToken, orderId);
        YandexKassaAPIService service = YandexKassaAPIService.getInstance();
        service.checkPayment(orderId, mCurrentPayment, this);
    }

    //Result of payment operation
    @Override
    public void onCheckPaymentResponse(Call<YandexKassaPayment> call, Response<YandexKassaPayment> response) {
        if (response.body() == null){
            //error
            try {
                mCurrentPayment.setErrorDescription(response.errorBody().string());
            } catch (IOException e) {
                mCurrentPayment.setErrorDescription("Exception when obtaining web service error: " + e.getMessage());
            }
            return;
        }
        mCurrentPayment.updateFromWebAPIResponse(response.body());
        processPaymentStatusResult();
    }

    //Payment operation failed
    @Override
    public void onCheckPaymentFailure(Call<YandexKassaPayment> call, Throwable t) {
        mCurrentPayment.setErrorDescription(t.getMessage());
    }

    public void processPaymentStatusResult() {
        if (!m3DSActivityShown
                && mCurrentPayment.getStatus() != null
                && mCurrentPayment.getStatus().equals("pending")
                && mCurrentPayment.getConfirmation() != null ){
            show3DSActivity();
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCounter++;
                updateResultStats();
                if (mFirstCheck &&
                        mCurrentPayment.getPaymentStatus() != PaymentStatuses.success){
                    mFirstCheck = false;
                    enableAutoRefreshPaymentStatus(true);
                }
            }
        });

    }

    private void updateResultStats(){
        mResult.setText(mCurrentPayment.getResultDescription());
        mResultError.setText(mCurrentPayment.getErrorDescription());
        if (mCounter > 0){
            String msg = "Payment status refreshed: " + String.valueOf(mCounter);
            mRetryCntr.setText(msg);
        }
        else
        {
            mRetryCntr.setText("");
        }
        if (mCurrentPayment.getPaymentStatus() == PaymentStatuses.success)
            enableAutoRefreshPaymentStatus(false);
    }

    private void plan2RefreshPaymentStatus(){
        if (!mAutoRefresh)
            return;
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        checkPaymentResultTask = new CheckPaymentResultTask();
        mTimer.schedule(checkPaymentResultTask, ConfigurationService.getInstance().getRetryInterval());
    }


    private void enableAutoRefreshPaymentStatus(boolean enable){
        mAutoRefresh = enable;
        StopRefreshBtn.setEnabled(enable);
        if (!enable)
            mLoadingLayout.setVisibility(View.GONE);

        plan2RefreshPaymentStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_3D_SECURE_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    // Аутентификация по 3-D Secure прошла успешно
                    break;
                case RESULT_CANCELED:
                    // Экран 3-D Secure был закрыт
                    break;
                case Checkout.RESULT_ERROR:
                    // Во время 3-D Secure произошла какая-то ошибка
                    //(например, нет соединения),
                    // более подробную информацию можно посмотреть в data:
                    // data.getIntExtra(Checkout.EXTRA_ERROR_CODE) — код ошибки из WebViewClient.ERROR_* или Checkout.ERROR_NOT_HTTPS_URL
                    // data.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION) — описание ошибки (может отсутствовать)
                    // data.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL) — URL, по которому произошла ошибка (может отсутствовать)
                    break;
            }
            if (mFirstCheck){
                mFirstCheck = false;
                enableAutoRefreshPaymentStatus(true);
            }
        }
    }

    private void onStopRefreshButtonClicked(){
        mLoadingLayout.setVisibility(View.GONE);
        mRunmeBtn.setEnabled(true);
        enableAutoRefreshPaymentStatus(false);
        if (mTimer != null){
            mTimer.cancel();
        }
    }

    private void show3DSActivity() {
        m3DSActivityShown = true;
        //Show 3d-secure activity
        Confirmation conf = mCurrentPayment.getConfirmation();
        Intent intent = null;
        try {
            intent = Checkout.create3dsIntent(
                    thisActivity,
                    new URL(conf.getConfirmationUrl()),
                    new URL((conf.getReturnUrl() == null || conf.getReturnUrl().length() == 0) ? ConfigurationService.getInstance().getMyBaseUrl() : conf.getReturnUrl())

            );
            startActivityForResult(intent, ACTIVITY_3D_SECURE_REQUEST);
        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    class CheckPaymentResultTask extends TimerTask {

        @Override
        public void run() {
            YandexKassaAPIService service = YandexKassaAPIService.getInstance();
            service.checkPayment(orderId, mCurrentPayment, thisActivity);
        }
    }

}
