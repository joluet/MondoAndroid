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

import java.util.Collections;

import rx.Observable;
import rx.schedulers.Schedulers;
import tech.jonas.mondoandroid.BuildConfig;
import tech.jonas.mondoandroid.api.ApiModule;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.model.Balance;
import tech.jonas.mondoandroid.api.model.Merchant;
import tech.jonas.mondoandroid.api.model.Transaction;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.ui.model.UiTransaction;
import tech.jonas.mondoandroid.ui.model.UiTransaction.DeclineReason;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

import static android.content.Context.MODE_PRIVATE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link tech.jonas.mondoandroid.features.home.HomePresenter}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class HomePresenterTest {

    // Mocks
    private SubscriptionManager mockSubscriptionManager;
    private HomeView mockHomeView;
    private OauthManager mockOauthManager;
    private MondoService mockMondoService;


    private HomeStringProvider stringProvider;
    private Preference<String> accessTokenPreference;
    private SchedulerProvider schedulerProvider;

    // SUT
    private HomePresenter homePresenter;

    @Before
    public void setUp() {
        mockSubscriptionManager = mock(SubscriptionManager.class);
        mockHomeView = mock(HomeView.class);
        mockOauthManager = mock(OauthManager.class);
        mockMondoService = mock(MondoService.class);

        stringProvider = new HomeStringProvider(RuntimeEnvironment.application);

        final SharedPreferences prefs = RuntimeEnvironment.application.getSharedPreferences("mondo", MODE_PRIVATE);
        final RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(prefs);
        accessTokenPreference = rxSharedPreferences.getString(ApiModule.PREF_KEY_ACCESS_TOKEN);

        schedulerProvider = new SchedulerProvider(Schedulers.immediate(), Schedulers.immediate());

        homePresenter = HomePresenterImpl.builder()
                .withSubscriptionManager(mockSubscriptionManager)
                .withStringProvider(stringProvider)
                .withView(mockHomeView)
                .withOauthManager(mockOauthManager)
                .withMondoService(mockMondoService)
                .withAccessToken(accessTokenPreference)
                .withSchedulerProvider(schedulerProvider)
                .build();
    }

    @Test
    public void shouldProceedToLogin() {
        accessTokenPreference.delete();
        homePresenter.onBindView(null);

        verify(mockHomeView).startLoginActivity();
    }

    @Test
    public void shouldDisplayCorrectBalance() {
        accessTokenPreference.set("some token string");
        final Balance balance = new Balance(1234L, "GBP", 2222L);
        when(mockMondoService.getBalance(anyString())).thenReturn(Observable.just(balance));
        when(mockMondoService.getTransactions(anyString(), anyString())).thenReturn(Observable.empty());

        homePresenter.onBindView(null);
        verify(mockHomeView).setTitle(eq(stringProvider.getFormattedBalance("£12.34 ")));
    }

    @Test
    public void shouldDisplayTransactions() {
        accessTokenPreference.set("some token string");
        final Balance balance = new Balance(1234L, "GBP", 2222L);
        final Merchant merchant = new Merchant("merchant name", "", "logo url");
        final Transaction transaction = new Transaction(1234L, "description", "CARD_BLOCKED", "GBP", "category", "created", 3244L, merchant);
        final TransactionList transactionList = new TransactionList(Collections.singletonList(transaction));
        when(mockMondoService.getBalance(anyString())).thenReturn(Observable.just(balance));
        when(mockMondoService.getTransactions(anyString(), anyString())).thenReturn(Observable.just(transactionList));

        homePresenter.onBindView(null);
        final UiTransaction expectedTransaction = new UiTransaction("£12.34 ", "description", "category", "created", DeclineReason.CARD_BLOCKED, "merchant name", "logo url");
        verify(mockHomeView).setTransactions(Collections.singletonList(expectedTransaction));
    }

}