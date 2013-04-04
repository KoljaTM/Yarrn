package de.vanmar.android.knitdroid.ravelry;

import org.scribe.model.OAuthRequest;

public interface IRavelryActivity {

	void callRavelry(final OAuthRequest request,
			final ResultCallback<String> callback);

}
