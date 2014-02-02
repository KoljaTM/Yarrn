package de.vanmar.android.knitdroid.ravelry;

import android.app.Application;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.TimeUnit;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.util.PrefsUtils;

public abstract class AbstractRavelryRequest<T> extends SpringAndroidSpiceRequest<T> {
    protected final Application application;
    protected final KnitdroidPrefs_ prefs;

    public AbstractRavelryRequest(Class<T> clazz, KnitdroidPrefs_ prefs, Application application) {
        super(clazz);
        this.prefs = prefs;
        this.application = application;
        setRetryPolicy(new DefaultRetryPolicy(1, 0, 0));
    }

    protected Response executeRequest(OAuthRequest request) throws RavelryException {
        if (PrefsUtils.isSet(prefs.accessToken())
                && PrefsUtils.isSet(prefs.username())) {
            signRequest(request);
            request.setConnectTimeout(10, TimeUnit.SECONDS);
            final Response response = request.send();
            switch (response.getCode()) {
                case 200:
                case 304:
                    return response;
                case 401:
                case 403:
                case 404:
                    throw new RavelryException(response.getCode());
                default:
                    throw new IllegalArgumentException(
                            "Unknown Response code: "
                                    + response.getCode());
            }
        } else {
            throw new RavelryException(401);
        }
    }

    private void signRequest(OAuthRequest request) {
        final String apiKey = application.getString(R.string.api_key);
        final String apiSecret = application.getString(R.string.api_secret);
        final String callback = application.getString(R.string.api_callback);
        OAuthService service = new ServiceBuilder()
                .provider(new RavelryApi(application.getString(R.string.ravelry_url)))
                .apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();

        final Token accessToken = new Token(prefs.accessToken()
                .get(), prefs.accessSecret().get());
        service.signRequest(accessToken, request);
        request.setConnectTimeout(10, TimeUnit.SECONDS);
    }
}
