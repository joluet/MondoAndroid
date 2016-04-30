# MondoAndroid

A basic Android app for [Mondo](https://getmondo.co.uk/). It lets you view your balance and a list of all transactions.

![A screenshot](./screenshot_home.png?raw=true)
![A screenshot](./screenshot_transaction.png?raw=true)

## Configuration
If you have developer access to the Mondo API you can create a _confidental_ auth client in the developer console.
Then, create a config file with your auth client details that looks like this:

``` java
package tech.jonas.mondoandroid.api;

public class OauthConfig {
    public static final String CLIENT_ID = <YOUR-OAUTH-CLIENT-ID>;
    public static final String CLIENT_SECRET = <YOUR-OAUTH-CLIENT-SECRET>;
}
```
Put that config file in the package `tech.jonas.mondoandroid.api` and you're good to go.

## Push Notifications
Please note that this app currently uses a small Sinatra server hosted with _Heroku_ to be able to receive push notifications when a new transaction is made. The source code of that Sinatra server app can be found [here](https://github.com/joluet/MondoGcmPush).
It just forwards the body of any transaction in form of a GCM push notification to the corresponding client.

## Build Instructions
Just use the gradle wrapper to build the app: `$ ./gradlew assembleDebug`
