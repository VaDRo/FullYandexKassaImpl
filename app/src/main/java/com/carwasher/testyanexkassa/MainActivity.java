package com.carwasher.testyanexkassa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
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

        isPermissionGranted();

    }

    //ВЫЗЫВАЕМ ОПЛАТУ
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
    @Override
    public void onResult(@NonNull String paymentToken, @NonNull PaymentMethodType type) {
        mCurrentPaymentToken = paymentToken;
        mCurrentPaymentMethod = type;
        enableAutoRefreshPaymentStatus(true);
        //Call Yandex.Kassa to process current payment OR to check payment result
        mCurrentPayment = new YandexKassaPayment(mAmount2pay, mCurrency,"Test payment", "https://washercar.ru", mCurrentPaymentMethod, mCurrentPaymentToken, orderId);
        YandexKassaAPIService service = YandexKassaAPIService.getInstance();
        service.checkPayment(orderId, mCurrentPayment, this);
    }

    private void onStopRefreshButtonClicked(){
        mLoadingLayout.setVisibility(View.GONE);
        mRunmeBtn.setEnabled(true);
        enableAutoRefreshPaymentStatus(false);
        if (mTimer != null){
            mTimer.cancel();
        }
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
        if (mCurrentPayment.getPaymentStatus() == PaymentStatuses.success
                //|| (mPaymentResult.getErrorCode() != null && !mPaymentResult.getErrorCode().isEmpty())
            )
            enableAutoRefreshPaymentStatus(false);

    }

    private void refreshPaymentStatus(){
        if (!mAutoRefresh)
            return;
        mTimer = new Timer();
        checkPaymentResultTask = new CheckPaymentResultTask();
        mTimer.schedule(checkPaymentResultTask, ConfigurationService.getInstance().getRetryInterval());
    }

    private void enableAutoRefreshPaymentStatus(boolean enable){
        mAutoRefresh = enable;
        StopRefreshBtn.setEnabled(enable);
        if (!enable)
            mLoadingLayout.setVisibility(View.GONE);

        refreshPaymentStatus();
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 2: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    //do ur specific task after read phone state granted
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

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
        CheckPaymentStatusResult();
    }

    @Override
    public void onCheckPaymentFailure(Call<YandexKassaPayment> call, Throwable t) {
        mCurrentPayment.setErrorDescription(t.getMessage());
    }

    public void CheckPaymentStatusResult() {
        if (mCurrentPayment.getStatus() != null
                && mCurrentPayment.getStatus().equals("pending")
                && mCurrentPayment.getConfirmation() != null ){
            //Show 3d-secure activity
            Confirmation conf = mCurrentPayment.getConfirmation();
            Intent intent = null;
            try {
                intent = Checkout.create3dsIntent(
                        this,
                        new URL(conf.getConfirmationUrl()),
                        new URL((conf.getReturnUrl() == null || conf.getReturnUrl().length() == 0) ? "http://yandex.ru" : conf.getReturnUrl())

                );
                startActivityForResult(intent, ACTIVITY_3D_SECURE_REQUEST);
            } catch (MalformedURLException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCounter++;
                updateResultStats();
                if (mAutoRefresh)
                {
                    refreshPaymentStatus();
                }
            }
        });
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
