package de.vanmar.android.knitdroid;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ParameterList;
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
import de.vanmar.android.knitdroid.util.NetworkHelper;
import de.vanmar.android.knitdroid.util.NetworkHelper_;
import de.vanmar.android.knitdroid.util.PrefsUtils;
import de.vanmar.android.knitdroid.util.RequestCode;
import de.vanmar.android.knitdroid.util.UiHelper;
import de.vanmar.android.knitdroid.util.UiHelper_;

public abstract class AbstractRavelryActivity extends FragmentActivity
		implements IRavelryActivity {

	public KnitdroidPrefs_ prefs;
	public NetworkHelper networkHelper;
	public UiHelper uiHelper;
	protected OAuthService service;
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
						request.setConnectTimeout(10, TimeUnit.SECONDS);
						final Response response = request.send();
						switch (response.getCode()) {
						case 200:
							callback.onSuccess(response.getBody());
							break;
						case 401:
						case 403:
						case 404:
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
					if (!networkHelper.networkAvailable()) {
						uiHelper.displayError(R.string.network_not_available);
						return;
					}
					if (e.getCause() instanceof SocketTimeoutException) {
						uiHelper.displayError(R.string.connection_timeout);
						return;
					}
					Log.e("AbstractRavelryActivity",
							"A runtime exception was thrown while executing code in a runnable",
							e);
					AQUtility.report(e);
				}
			}

		});
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RequestCode.REQUEST_CODE_GET_TOKEN
				&& resultCode == RESULT_OK) {
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
		networkHelper = NetworkHelper_.getInstance_(this);
		uiHelper = UiHelper_.getInstance_(this);
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
		final ParameterList bodyParams = request.getBodyParams();
		if (bodyParams != null) {
			recreatedRequest.getBodyParams().addAll(bodyParams);
		}
		final ParameterList queryStringParams = request.getQueryStringParams();
		if (queryStringParams != null) {
			recreatedRequest.getQueryStringParams().addAll(queryStringParams);
		}
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
				GetAccessTokenActivity_.class),
				RequestCode.REQUEST_CODE_GET_TOKEN);
	}

}