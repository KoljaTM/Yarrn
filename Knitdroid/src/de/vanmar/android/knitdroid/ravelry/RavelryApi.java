package de.vanmar.android.knitdroid.ravelry;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class RavelryApi extends DefaultApi10a {

	@Override
	public String getRequestTokenEndpoint() {
		return "https://www.ravelry.com/oauth/request_token";
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "https://www.ravelry.com/oauth/access_token";
	}

	@Override
	public String getAuthorizationUrl(final Token requestToken) {
		return String.format(
				"https://www.ravelry.com/oauth/authorize?oauth_token=%s",
				new Object[] { requestToken.getToken() });
	}
}