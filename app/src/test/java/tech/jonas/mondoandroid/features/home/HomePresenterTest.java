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

import tech.jonas.mondoandroid.BuildConfig;
import tech.jonas.mondoandroid.api.ApiModule;
import tech.jonas.mondoandroid.api.MondoService;
import tech.jonas.mondoandroid.api.authentication.OauthManager;

import static android.content.Context.MODE_PRIVATE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link tech.jonas.mondoandroid.features.home.HomePresenter}.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class HomePresenterTest {

    // Mocks
    private SubscriptionManager mockSubscriptionManager;
    private HomeStringProvider mockStringProvider;
    private HomeView mockHomeView;
    private OauthManager mockOauthManager;
    private MondoService mockMondoService;


    private Preference<String> accessTokenPreference;

    // SUT
    private HomePresenter homePresenter;

    @Before
    public void setUp() {
        mockSubscriptionManager = mock(SubscriptionManager.class);
        mockStringProvider = mock(HomeStringProvider.class);
        mockHomeView = mock(HomeView.class);
        mockOauthManager = mock(OauthManager.class);
        mockMondoService = mock(MondoService.class);

        final SharedPreferences prefs = RuntimeEnvironment.application.getSharedPreferences("mondo", MODE_PRIVATE);
        final RxSharedPreferences rxSharedPreferences = RxSharedPreferences.create(prefs);
        accessTokenPreference = rxSharedPreferences.getString(ApiModule.PREF_KEY_ACCESS_TOKEN);

        homePresenter = HomePresenterImpl.builder()
                .withSubscriptionManager(mockSubscriptionManager)
                .withStringProvider(mockStringProvider)
                .withView(mockHomeView)
                .withOauthManager(mockOauthManager)
                .withMondoService(mockMondoService)
                .withAccessToken(accessTokenPreference)
                .build();
    }

    @Test
    public void shouldProceedToLogin() {
        accessTokenPreference.delete();
        homePresenter.onBindView(null);

        verify(mockHomeView).startLoginActivity();
    }

}