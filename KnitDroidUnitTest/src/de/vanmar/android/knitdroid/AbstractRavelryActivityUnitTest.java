package de.vanmar.android.knitdroid;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.scribe.model.Response;
import org.scribe.oauth.OAuthService;

import android.content.Intent;
import de.vanmar.android.knitdroid.mocking.OAuthRequestForMocking;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.util.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class AbstractRavelryActivityUnitTest {

	private AbstractRavelryActivity activity;

	@Mock
	private OAuthRequestForMocking request;

	@Mock
	private ResultCallback<String> callback;

	@Mock
	private OAuthService oauthService;

	@Mock
	private Response response;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);

		activity = new AbstractRavelryActivity() {
		};
		activity.setIntent(new Intent());
		activity.onCreate(null);
		activity.service = oauthService;

		TestUtil.mockBackgroundExecutor();
		TestUtil.mockValidUser(activity.prefs);
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
}
