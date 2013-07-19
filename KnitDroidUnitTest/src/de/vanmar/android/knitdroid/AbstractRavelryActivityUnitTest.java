package de.vanmar.android.knitdroid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Response;
import org.scribe.oauth.OAuthService;

import android.content.Intent;

import com.androidquery.util.AQUtility;

import de.vanmar.android.knitdroid.mocking.OAuthRequestForMocking;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.util.NetworkHelper;
import de.vanmar.android.knitdroid.util.TestUtil;
import de.vanmar.android.knitdroid.util.UiHelper;

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

		activity = new AbstractRavelryActivity() {
		};
		activity.setIntent(new Intent());
		activity.onCreate(null);
		activity.service = oauthService;
		activity.networkHelper = networkHelper;
		activity.uiHelper = uiHelper;

		TestUtil.mockBackgroundExecutor();
		TestUtil.mockValidUser(activity.prefs);

		AQUtility.setExceptionHandler(exceptionHandler);
	}

	@Test
	public void shouldExecuteRequest() throws Exception {
		// given
		given(request.send()).willReturn(response);
		given(response.getCode()).willReturn(200);

		// when
		activity.callRavelry(request, callback);

		// then
		verify(callback).onSuccess(anyString());
	}

	@Test
	public void shouldRedirectToLoginOnRequestError() {
		// given
		given(request.send()).willReturn(response);
		given(response.getCode()).willReturn(403);

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
	}

	@Test
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
	}

	@Test
	public void shouldReportErrors() {
		// given
		final RuntimeException testException = new RuntimeException(
				"Test Exception");
		given(request.send()).willThrow(testException);
		given(networkHelper.networkAvailable()).willReturn(true);

		// when
		activity.callRavelry(request, callback);

		// then
		verify(exceptionHandler).uncaughtException(any(Thread.class),
				same(testException));
	}

	@Test
	public void shouldNotReportExceptionIfInternetIsDown() {
		// given
		final RuntimeException testException = new RuntimeException(
				"Test Exception");
		given(request.send()).willThrow(testException);
		given(networkHelper.networkAvailable()).willReturn(false);

		// when
		activity.callRavelry(request, callback);

		// then
		verify(exceptionHandler, never()).uncaughtException(any(Thread.class),
				any(Exception.class));
		verify(uiHelper).displayError(R.string.network_not_available);
	}

	@Test
	public void shouldNotReportSocketTimeoutException() {
		// given
		final RuntimeException testException = new OAuthException(
				"Test Exception", new SocketTimeoutException("Test Exception"));
		given(request.send()).willThrow(testException);
		given(networkHelper.networkAvailable()).willReturn(true);

		// when
		activity.callRavelry(request, callback);

		// then
		verify(exceptionHandler, never()).uncaughtException(any(Thread.class),
				any(Exception.class));
		verify(uiHelper).displayError(R.string.connection_timeout);
	}
}
