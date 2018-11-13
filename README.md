# FullYandexKassaImpl
Full Yandex.Kassa implementation without web server support

This project intended to help Android developers to create payment solution with Yandex.Kassa API without web server middle layer. All communication with Yandex.Kassa was realized on Android side

What do you need to have this app running:
1. Register on Yandex.Kassa service https://kassa.yandex.ru
2. Create your market on that service
3. Initialize your account to work through web API
4. Create secret API key + secret App key and wait them to be activated
5. Update constants in ConfigurationService class:
   a) YandexKassaShopId = ID of your market on Yandex.Kassa service
   b) YandexKassaMSDKPrivateKey = secret App key of your market
   c) YandexKassaPrivateKey = secret API key
6. All done :)

Please, be sure you filled constants on step 5 in right way!

# General algorythm
You need to use 2 APIs in general: Yandex.Kassa Web API and Yandex.Kassa Mobile SDK
First of all, you need to create payment token to process payment with mobile SDK.
After that you should call Web API to create payment. The same method of Web API should be called to check payment results

# WARNING!
In current version I can't process 3-D Secure check!
This option is under construction.
