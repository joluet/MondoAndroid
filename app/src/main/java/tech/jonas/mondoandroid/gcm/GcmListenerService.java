package tech.jonas.mondoandroid.gcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import javax.inject.Inject;

import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.api.model.PushMessage;
import tech.jonas.mondoandroid.di.ComponentProvider;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.features.home.MainActivity;
import tech.jonas.mondoandroid.ui.model.TransactionMapper;
import tech.jonas.mondoandroid.ui.model.UiTransaction;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    @Inject Gson gson;
    @Inject HomeStringProvider stringProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        ApiComponent apiComponent = ((ComponentProvider<ApiComponent>) getApplicationContext()).getComponent();
        apiComponent.inject(this);

    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        PushMessage pushMessage = gson.fromJson(message, PushMessage.class);
        final UiTransaction transaction = TransactionMapper.map(stringProvider, pushMessage.transaction);

        sendNotification(transaction);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param transaction UiTransaction object received.
     */
    private void sendNotification(UiTransaction transaction) {
        final String message = getString(R.string.amount_and_merchant, transaction.formattedAmount, transaction.merchantName);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

}
