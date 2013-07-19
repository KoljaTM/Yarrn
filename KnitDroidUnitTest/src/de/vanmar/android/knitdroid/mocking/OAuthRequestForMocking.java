package de.vanmar.android.knitdroid.mocking;

import org.scribe.model.OAuthRequest;
import org.scribe.model.ParameterList;
import org.scribe.model.Response;
import org.scribe.model.Verb;

/**
 * @author Kolja
 * 
 *         This class is needed to work around a limitation of Mockito/Java that
 *         prevents Mockito from mocking classes whose parent classes are not
 *         public. See also
 *         http://code.google.com/p/mockito/issues/detail?id=212
 * 
 *         Any methods that should be mocked have to be added as a delegate
 *         here.
 */
public class OAuthRequestForMocking extends OAuthRequest {

	public OAuthRequestForMocking(final Verb verb, final String url) {
		super(verb, url);
	}

	@Override
	public Response send() {
		return super.send();
	}

	@Override
	public ParameterList getBodyParams() {
		return super.getBodyParams();
	}

	@Override
	public ParameterList getQueryStringParams() {
		return super.getQueryStringParams();
	}

	@Override
	public String getUrl() {
		return super.getUrl();
	}

	@Override
	public Verb getVerb() {
		return super.getVerb();
	}

}
