package de.vanmar.android.knitdroid;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.androidquery.util.AQUtility;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryApi;
import de.vanmar.android.knitdroid.util.NetworkHelper;
import de.vanmar.android.knitdroid.util.NetworkHelper_;
import de.vanmar.android.knitdroid.util.RequestCode;
import de.vanmar.android.knitdroid.util.UiHelper;
import de.vanmar.android.knitdroid.util.UiHelper_;

public abstract class AbstractRavelryActivity extends SherlockFragmentActivity
implements IRavelryActivity {

	public KnitdroidPrefs_ prefs;
	public NetworkHelper networkHelper;
	public UiHelper uiHelper;
	protected OAuthService service;

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
	}

	@Override
	public void requestToken() {
		startActivityForResult(new Intent(AbstractRavelryActivity.this,
				GetAccessTokenActivity_.class),
				RequestCode.REQUEST_CODE_GET_TOKEN);
	}

}