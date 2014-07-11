package de.vanmar.android.yarrn.ravelry;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidquery.util.AQUtility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;

@EActivity(resName = "activity_get_access")
public class GetAccessTokenActivity extends Activity {

    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_ACCESSTOKEN = "accessToken";
    public static final String EXTRA_ACCESSSECRET = "accessSecret";
    public static final String EXTRA_REQUESTTOKEN = "requestToken";

    @Pref
    YarrnPrefs_ prefs;

    @UiThread
    protected void callAuthPage(final String callback,
                                final OAuthService service, final Token requestToken,
                                final String authURL) {
        final WebView webview = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            webview.getSettings().setSaveFormData(false);
        }

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(final WebView view,
                                           final SslErrorHandler handler, final SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {

                if (url.startsWith(callback)) {
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

        try {
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
        } catch (OAuthException e) {
            reportOAuthError();
        }
    }

    @UiThread
    public void reportOAuthError() {
        Toast.makeText(getApplicationContext(), R.string.oauth_exception,
                Toast.LENGTH_LONG).show();
    }

    @Background
    public void getToken() {
        final String apiKey = getString(R.string.api_key);
        final String apiSecret = getString(R.string.api_secret);
        final String callback = getString(R.string.api_callback);
        final OAuthService service = new ServiceBuilder()
                .provider(new RavelryApi(getString(R.string.ravelry_url)))
                .apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();

        final Token requestToken;
        try {
            requestToken = service.getRequestToken();
        } catch (Exception e) {
            AQUtility.report(e);
            moveTaskToBack(true);
            return;
        }
        final String authURL = service.getAuthorizationUrl(requestToken);

        callAuthPage(callback, service, requestToken, authURL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getToken();
    }

}
