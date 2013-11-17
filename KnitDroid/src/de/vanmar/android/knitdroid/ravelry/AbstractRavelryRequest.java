package de.vanmar.android.knitdroid.ravelry;

import android.content.Context;
import com.octo.android.robospice.request.SpiceRequest;
import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.util.PrefsUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.TimeUnit;

public abstract class AbstractRavelryRequest<T> extends SpiceRequest<T> {
	protected final Context context;
	protected final KnitdroidPrefs_ prefs;

	public AbstractRavelryRequest(Class<T> clazz, KnitdroidPrefs_ prefs, Context context) {
		super(clazz);
		this.prefs = prefs;
		this.context = context;
	}

	protected Response executeRequest(OAuthRequest request) throws RavelryException {
		if (PrefsUtils.isSet(prefs.accessToken())
				&& PrefsUtils.isSet(prefs.username())) {
			signRequest(request);
			request.setConnectTimeout(10, TimeUnit.SECONDS);
			final Response response = request.send();
			switch (response.getCode()) {
				case 200:
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
		final String apiKey = context.getString(R.string.api_key);
		final String apiSecret = context.getString(R.string.api_secret);
		final String callback = context.getString(R.string.api_callback);
		OAuthService service = new ServiceBuilder()
				.provider(new RavelryApi(context.getString(R.string.ravelry_url)))
				.apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();

		final Token accessToken = new Token(prefs.accessToken()
				.get(), prefs.accessSecret().get());
		service.signRequest(accessToken, request);
		request.setConnectTimeout(10, TimeUnit.SECONDS);
	}
}
