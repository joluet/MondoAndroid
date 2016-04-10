package tech.jonas.mondoandroid.features.home;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class SubscriptionManager {

    CompositeSubscription subscriptions;

    public SubscriptionManager() {
        subscriptions = new CompositeSubscription();
    }


    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
