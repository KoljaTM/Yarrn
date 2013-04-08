package de.vanmar.android.knitdroid.ravelry;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class RavelryApi extends DefaultApi10a {

	private String ravelryUrl;

	public RavelryApi(final String ravelryUrl) {
		super();
		this.ravelryUrl = ravelryUrl;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return ravelryUrl + "/oauth/access_token";
	}

	@Override
	public String getAuthorizationUrl(final Token requestToken) {
		return String.format(ravelryUrl + "/oauth/authorize?oauth_token=%s",
				new Object[] { requestToken.getToken() });
	}

	@Override
	public String getRequestTokenEndpoint() {
		return ravelryUrl + "/oauth/request_token";
	}
}