package tech.jonas.mondoandroid.features.home;

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
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;
import tech.jonas.mondoandroid.api.model.Balance;
import tech.jonas.mondoandroid.api.model.Merchant;
import tech.jonas.mondoandroid.api.model.Transaction;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.ui.model.Spending;
import tech.jonas.mondoandroid.ui.model.UiTransaction;
import tech.jonas.mondoandroid.ui.model.UiTransaction.DeclineReason;
import tech.jonas.mondoandroid.utils.SchedulerProvider;

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

        schedulerProvider = new SchedulerProvider(Schedulers.immediate(), Schedulers.immediate());

        homePresenter = HomePresenterImpl.builder()
                .withSubscriptionManager(mockSubscriptionManager)
                .withStringProvider(stringProvider)
                .withView(mockHomeView)
                .withOauthManager(mockOauthManager)
                .withMondoService(mockMondoService)
                .withSchedulerProvider(schedulerProvider)
                .build();
    }

    @Test
    public void shouldProceedToLogin() {
        when(mockOauthManager.isAuthenticated()).thenReturn(false);
        homePresenter.onBindView(null);

        verify(mockHomeView).startLoginActivity();
    }

    @Test
    public void shouldDisplayCorrectBalance() {
        when(mockOauthManager.isAuthenticated()).thenReturn(true);
        final Balance balance = new Balance(1234L, "GBP", 2222L);
        when(mockMondoService.getBalance(anyString())).thenReturn(Observable.just(balance));
        when(mockMondoService.getTransactions(anyString(), anyString())).thenReturn(Observable.empty());

        homePresenter.onBindView(null);
        verify(mockHomeView).setTitle(eq(stringProvider.getFormattedBalance("£12.34 ")));
    }

    @Test
    public void shouldDisplayTransactions() {
        when(mockOauthManager.isAuthenticated()).thenReturn(true);
        final Balance balance = new Balance(1234L, "GBP", 2222L);
        final Merchant merchant = new Merchant("id", "merchant name", "", "logo url");
        final Transaction transaction = new Transaction("0", 1234L, "description", null, "GBP", "category", "created", 3244L, merchant);
        final TransactionList transactionList = new TransactionList(Collections.singletonList(transaction));
        when(mockMondoService.getBalance(anyString())).thenReturn(Observable.just(balance));
        when(mockMondoService.getTransactions(anyString(), anyString())).thenReturn(Observable.just(transactionList));

        homePresenter.onBindView(null);
        final UiTransaction expectedTransaction = new UiTransaction("0", "£12.34 ", "description", "category", "created", null, "merchant name", "logo url", new Spending(1234L, 1234L, 1));
        verify(mockHomeView).setTransactions(Collections.singletonList(expectedTransaction));
    }

}