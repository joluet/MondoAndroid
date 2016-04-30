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

## Build Instructions
Just use the gradle wrapper to build the app: `$ ./gradlew assembleDebug`