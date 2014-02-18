package de.vanmar.android.yarrn;

import android.app.Activity;
import android.content.Intent;

import com.androidquery.util.AQUtility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;
import org.scribe.model.Response;
import org.scribe.oauth.OAuthService;

import java.lang.Thread.UncaughtExceptionHandler;

import de.vanmar.android.yarrn.mocking.OAuthRequestForMocking;
import de.vanmar.android.yarrn.ravelry.GetAccessTokenActivity;
import de.vanmar.android.yarrn.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.yarrn.ravelry.ResultCallback;
import de.vanmar.android.yarrn.util.NetworkHelper;
import de.vanmar.android.yarrn.util.RequestCode;
import de.vanmar.android.yarrn.util.TestUtil;
import de.vanmar.android.yarrn.util.UiHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AbstractRavelryActivityUnitTest {

    private AbstractRavelryActivity activity;

    @Mock
    private OAuthService oauthService;

    @Mock
    private NetworkHelper networkHelper;

    @Mock
    private UiHelper uiHelper;

    @Mock
    private OAuthRequestForMocking request;

    @Mock
    private ResultCallback<String> callback;

    @Mock
    private Response response;

    @Mock
    private UncaughtExceptionHandler exceptionHandler;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);

        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class);
        activityController.create();
        activity = activityController.get();

        activity.service = oauthService;
        activity.networkHelper = networkHelper;
        activity.uiHelper = uiHelper;

        TestUtil.mockBackgroundExecutor();
        TestUtil.mockValidUser(activity.prefs);

        AQUtility.setExceptionHandler(exceptionHandler);
    }

    @Test
    public void shouldRequestToken() {
        // when
        activity.requestToken();

        // then
        ShadowActivity.IntentForResult startedActivityForResult = Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertNotNull("should start activity", startedActivityForResult);
        assertTrue(((Object) startedActivityForResult.intent).equals(new Intent(activity,
                GetAccessTokenActivity_.class)));
    }

    @Test
    public void shouldSaveTokenAndUsernameFromAccessTokenActivity() {
        // given
        Intent intent = new Intent();
        String username = "username";
        String accesstoken = "accesstoken";
        String accesssecret = "accesssecret";
        String requesttoken = "requesttoken";
        intent.putExtra(GetAccessTokenActivity.EXTRA_USERNAME, username);
        intent.putExtra(GetAccessTokenActivity.EXTRA_ACCESSTOKEN, accesstoken);
        intent.putExtra(GetAccessTokenActivity.EXTRA_ACCESSSECRET, accesssecret);
        intent.putExtra(GetAccessTokenActivity.EXTRA_REQUESTTOKEN, requesttoken);

        // when
        activity.onActivityResult(RequestCode.REQUEST_CODE_GET_TOKEN, Activity.RESULT_OK, intent);

        // then
        assertThat(activity.prefs.username().get(), is(username));
        assertThat(activity.prefs.accessToken().get(), is(accesstoken));
        assertThat(activity.prefs.accessSecret().get(), is(accesssecret));
        assertThat(activity.prefs.requestToken().get(), is(requesttoken));
    }

/*	@Test
    public void shouldRedirectToLoginIfUsernameNotFound() {
		// given
		given(request.send()).willReturn(response);
		given(response.getCode()).willReturn(200);
		activity.prefs.username().put("");

		// when
		activity.callRavelry(request, callback);

		// then
		verify(callback, never()).onSuccess(anyString());
		final Intent nextStartedActivity = Robolectric.shadowOf(activity)
				.getNextStartedActivity();
		assertNotNull(nextStartedActivity);
		assertEquals(
				Robolectric.shadowOf(nextStartedActivity).getIntentClass(),
				GetAccessTokenActivity_.class);
	}*/
}
