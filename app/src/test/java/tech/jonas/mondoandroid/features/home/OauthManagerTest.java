package tech.jonas.mondoandroid.features.home;

import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import tech.jonas.mondoandroid.BuildConfig;
import tech.jonas.mondoandroid.api.ApiModule;
import tech.jonas.mondoandroid.api.GcmService;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.model.AccessTokenResponse;
import tech.jonas.mondoandroid.data.IntentFactory;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

import static android.content.Context.MODE_PRIVATE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link tech.jonas.mondoandroid.api.authentication.OauthManager}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OauthManagerTest {

    // Mocks
    private SubscriptionManager mockSubscriptionManager;
    private IntentFactory mockIntentFactory;
    private MondoService mockMondoService;
    private GcmService mockGcmService;


    private HomeStringProvider stringProvider;
    private Preference<String> accessTokenPreference;
    private Preference<String> refreshTokenPreference;
    private SchedulerProvider schedulerProvider;

    // SUT
    private OauthManager oauthManager;

    @Before
    public void setUp() {
        mockSubscriptionManager = mock(SubscriptionManager.class);
        mockIntentFactory = mock(IntentFactory.class);
        mockMondoService = mock(MondoService.class);
        mockGcmService = mock(GcmService.class);

        stringProvider = new HomeStringProvider(RuntimeEnvironment.application);

        final SharedPreferences prefs = RuntimeEnvironment.application.getSharedPreferences("mondo", MODE_PRIVATE);
        final RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(prefs);
        accessTokenPreference = rxSharedPreferences.getString(ApiModule.PREF_KEY_ACCESS_TOKEN);
        refreshTokenPreference = rxSharedPreferences.getString(ApiModule.PREF_KEY_ACCESS_TOKEN);

        schedulerProvider = new SchedulerProvider(Schedulers.immediate(), Schedulers.immediate());

        oauthManager = new OauthManager(mockIntentFactory, mockMondoService, mockGcmService, accessTokenPreference,
                refreshTokenPreference, RuntimeEnvironment.application);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldOnlyRefreshTokenOnce() {
        final ConnectableObservable<AccessTokenResponse> tokenObservable = Observable
                .just(new AccessTokenResponse("access", "refresh")).publish();
        when(mockMondoService.getAccessToken(any(Map.class)))
                .thenReturn(tokenObservable);

        TestSubscriber<String> subscriber1 = new TestSubscriber<>();
        Observable<String> o1 = oauthManager.refreshAuthToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());

        TestSubscriber<String> subscriber2 = new TestSubscriber<>();
        Observable<String> o2 = oauthManager.refreshAuthToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());

        TestSubscriber<String> subscriber3 = new TestSubscriber<>();
        Observable<String> o3 = oauthManager.refreshAuthToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());

        o1.subscribe(subscriber1);
        o2.subscribe(subscriber2);
        o3.subscribe(subscriber3);

        // Emit item from observable
        tokenObservable.connect();

        // Wait for all subscribers
        subscriber1.awaitTerminalEvent(1, TimeUnit.SECONDS);
        subscriber2.awaitTerminalEvent(1, TimeUnit.SECONDS);
        subscriber3.awaitTerminalEvent(1, TimeUnit.SECONDS);

        // Make sure refresh token call is only executed once
        verify(mockMondoService, times(1)).getAccessToken(any(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRefreshTokenTwice() {
        when(mockMondoService.getAccessToken(any(Map.class)))
                .thenReturn(Observable.just(new AccessTokenResponse("access", "refresh")));

        TestSubscriber<String> subscriber1 = new TestSubscriber<>();
        oauthManager.refreshAuthToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber1);
        subscriber1.awaitTerminalEvent(1, TimeUnit.SECONDS);

        TestSubscriber<String> subscriber2 = new TestSubscriber<>();
        oauthManager.refreshAuthToken()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(subscriber2);
        subscriber2.awaitTerminalEvent(1, TimeUnit.SECONDS);

        verify(mockMondoService, times(2)).getAccessToken(any(Map.class));
    }

}