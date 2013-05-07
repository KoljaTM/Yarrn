package de.vanmar.android.knitdroid;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.api.BackgroundExecutor;

import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryApi;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.util.PrefsUtils;

public abstract class AbstractRavelryActivity extends FragmentActivity
		implements IRavelryActivity {

	private static final int REQUEST_CODE = 1;

	public KnitdroidPrefs_ prefs;
	private OAuthService service;
	private Runnable waitingToExecute = null;

	@Override
	public void callRavelry(final OAuthRequest request,
			final ResultCallback<String> callback) {
		BackgroundExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					if (PrefsUtils.isSet(prefs.accessToken())
							&& PrefsUtils.isSet(prefs.username())) {
						final Token accessToken = new Token(prefs.accessToken()
								.get(), prefs.accessSecret().get());
						service.signRequest(accessToken, request);
						final Response response = request.send();
						switch (response.getCode()) {
						case 200:
							callback.onSuccess(response.getBody());
							break;
						case 401:
						case 403:
							requestTokenForRequest(recreateRequest(request),
									callback);
							break;
						default:
							throw new IllegalArgumentException(
									"Unknown Response code: "
											+ response.getCode());
						}
					} else {
						requestTokenForRequest(request, callback);
					}
				} catch (final RuntimeException e) {
					Log.e("AbstractRavelryActivity",
							"A runtime exception was thrown while executing code in a runnable",
							e);
				}
			}

		});
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			prefs.username().put(
					data.getStringExtra(GetAccessTokenActivity.EXTRA_USERNAME));
			prefs.accessToken()
					.put(data
							.getStringExtra(GetAccessTokenActivity.EXTRA_ACCESSTOKEN));
			prefs.accessSecret()
					.put(data
							.getStringExtra(GetAccessTokenActivity.EXTRA_ACCESSSECRET));
			prefs.requestToken()
					.put(data
							.getStringExtra(GetAccessTokenActivity.EXTRA_REQUESTTOKEN));
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String apiKey = getString(R.string.api_key);
		final String apiSecret = getString(R.string.api_secret);
		final String callback = getString(R.string.api_callback);
		service = new ServiceBuilder()
				.provider(new RavelryApi(getString(R.string.ravelry_url)))
				.apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();

		prefs = new KnitdroidPrefs_(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isTaskRoot()) {
			AQUtility.cleanCacheAsync(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (waitingToExecute != null) {
			waitingToExecute.run();
			waitingToExecute = null;
		}
	}

	private OAuthRequest recreateRequest(final OAuthRequest request) {
		final OAuthRequest recreatedRequest = new OAuthRequest(
				request.getVerb(), request.getUrl());
		recreatedRequest.getBodyParams().addAll(request.getBodyParams());
		recreatedRequest.getQueryStringParams().addAll(
				request.getQueryStringParams());
		return recreatedRequest;
	}

	private void requestTokenForRequest(final OAuthRequest request,
			final ResultCallback<String> callback) {
		waitingToExecute = new Runnable() {
			@Override
			public void run() {
				callRavelry(request, callback);
			}
		};
		startActivityForResult(new Intent(AbstractRavelryActivity.this,
				GetAccessTokenActivity_.class), REQUEST_CODE);
	}

}