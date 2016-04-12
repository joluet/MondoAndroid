package tech.jonas.mondoandroid.utils;

import rx.Observable;
import rx.Scheduler;

public class SchedulerProvider {
    private final Scheduler foregroundScheduler;
    private final Scheduler backgroundScheduler;

    public SchedulerProvider(Scheduler foregroundScheduler, Scheduler backgroundScheduler) {
        this.foregroundScheduler = foregroundScheduler;
        this.backgroundScheduler = backgroundScheduler;
    }

    @SuppressWarnings("unchecked")
    public <T> Observable.Transformer<T, T> getSchedulers() {
        return observable -> observable.subscribeOn(backgroundScheduler)
                .observeOn(foregroundScheduler);
    }
}
