package de.vanmar.android.knitdroid;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.projects.ProjectsFragment.ProjectsFragmentListener;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.ravelry.RavelryApi;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;

@EActivity(resName = "activity_main")
public class MainActivity extends FragmentActivity implements
		ProjectsFragmentListener {

	private static final int REQUEST_CODE = 1;

	@Pref
	KnitdroidPrefs_ prefs;

	private OAuthService service;

	private Runnable waitingToExecute = null;

	@Override
	@Background
	public void callRavelry(final OAuthRequest request,
			final ResultCallback<String> callback) {
		if (prefs.accessToken().exists()) {
			final Token accessToken = new Token(prefs.accessToken().get(),
					prefs.accessSecret().get());
			service.signRequest(accessToken, request);
			final Response response = request.send();
			callback.onSuccess(response.getBody());
		} else {
			waitingToExecute = new Runnable() {
				@Override
				public void run() {
					callRavelry(request, callback);
				}
			};
			startActivityForResult(new Intent(this,
					GetAccessTokenActivity_.class), REQUEST_CODE);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
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
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String apiKey = getString(R.string.api_key);
		final String apiSecret = getString(R.string.api_secret);
		final String callback = getString(R.string.api_callback);
		service = new ServiceBuilder().provider(RavelryApi.class)
				.apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();
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

	@Override
	public void onProjectSelected(final Integer projectId) {
		Toast.makeText(this, "Project " + projectId + " selected!",
				Toast.LENGTH_LONG).show();
	}
}
