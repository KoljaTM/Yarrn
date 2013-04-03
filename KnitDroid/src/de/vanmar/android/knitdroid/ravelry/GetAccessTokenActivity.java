package de.vanmar.android.knitdroid.ravelry;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;

@EActivity(resName = "activity_get_access")
public class GetAccessTokenActivity extends Activity {

	public static final String EXTRA_USERNAME = "username";
	public static final String EXTRA_ACCESSTOKEN = "accessToken";
	public static final String EXTRA_ACCESSSECRET = "accessSecret";
	public static final String EXTRA_REQUESTTOKEN = "requestToken";

	@Pref
	KnitdroidPrefs_ prefs;

	@UiThread
	protected void callAuthPage(final String callback,
			final OAuthService service, final Token requestToken,
			final String authURL) {
		final WebView webview = (WebView) findViewById(R.id.webview);

		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {

				if (url.startsWith(callback)) {
					webview.setVisibility(View.GONE);
					getAccessToken(service, requestToken, url);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

		});
		webview.loadUrl(authURL);
	}

	@Background
	protected void getAccessToken(final OAuthService service,
			final Token requestToken, final String url) {
		final Uri uri = Uri.parse(url);
		final String verifier = uri.getQueryParameter("oauth_verifier");
		final String username = uri.getQueryParameter("username");
		final Verifier v = new Verifier(verifier);

		final Token accessToken = service.getAccessToken(requestToken, v);

		prefs.username().put(username);
		prefs.accessToken().put(accessToken.getToken());
		prefs.accessSecret().put(accessToken.getSecret());
		prefs.requestToken().put(requestToken.getToken());

		final Intent result = new Intent();
		result.putExtra(EXTRA_USERNAME, username);
		result.putExtra(EXTRA_ACCESSTOKEN, accessToken.getToken());
		result.putExtra(EXTRA_ACCESSSECRET, accessToken.getSecret());
		result.putExtra(EXTRA_REQUESTTOKEN, requestToken.getToken());
		setResult(Activity.RESULT_OK, result);
		finish();
	}

	@Background
	public void getToken() {
		final String apiKey = getString(R.string.api_key);
		final String apiSecret = getString(R.string.api_secret);
		final String callback = getString(R.string.api_callback);
		final OAuthService service = new ServiceBuilder()
				.provider(RavelryApi.class).apiKey(apiKey).apiSecret(apiSecret)
				.callback(callback).build();

		final Token requestToken = service.getRequestToken();
		final String authURL = service.getAuthorizationUrl(requestToken);

		callAuthPage(callback, service, requestToken, authURL);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getToken();
	}

}
