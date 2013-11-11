package de.vanmar.android.knitdroid.projects;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.googlecode.androidannotations.annotations.*;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryApi;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.TimeUnit;

@EFragment(R.layout.fragment_projects)
public class ProjectsFragment extends Fragment {

	protected SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

	public interface ProjectsFragmentListener extends IRavelryActivity {
		/**
		 * Project with projectId was selected, 0 if no project selected
		 */
		void onProjectSelected(int projectId);
	}

	@ViewById(R.id.projectlist)
	ListView projectlist;

	@Pref
	KnitdroidPrefs_ prefs;

	private ProjectListAdapter adapter;

	private ProjectsFragmentListener listener;

	@AfterViews
	public void afterViews() {
		adapter = new ProjectListAdapter(getActivity()) {

			@Override
			protected void onProjectClicked(final JSONObject projectJson) {
				listener.onProjectSelected(projectJson.optInt("id"));
			}

		};
		projectlist.setAdapter(adapter);
	}

	@UiThread
	protected void displayProjects(final String result) {
		try {
			final JSONObject json = new JSONObject(result);
			adapter.setData(json.getJSONArray("projects"));
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Background
	public void getProjects(final ResultCallback<String> callback) {
		final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
				getString(R.string.ravelry_url) + "/projects/%s/list.json",
				prefs.username().get()));
		listener.callRavelry(request, callback);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ProjectsFragmentListener) {
			listener = (ProjectsFragmentListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement ProjectsFragmentListener");
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public void onStart() {
		super.onStart();
		spiceManager.start(this.getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			spiceManager.execute(new ProjectsRequest(), new ProjectsListener());
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} finally {
		}
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

	/*@Override
	public void onStart() {
		super.onStart();


		getProjects(new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				AQUtility.report(exception);
			}

			@Override
			public void onSuccess(final String result) {
				displayProjects(result);
			}
		});
	}      */

	class ProjectsRequest extends SpiceRequest<ProjectResult> {

		public ProjectsRequest() {
			super(ProjectResult.class);
		}

		@Override
		public ProjectResult loadDataFromNetwork() throws Exception {
			final String apiKey = getString(R.string.api_key);
			final String apiSecret = getString(R.string.api_secret);
			final String callback = getString(R.string.api_callback);
			OAuthService service = new ServiceBuilder()
					.provider(new RavelryApi(getString(R.string.ravelry_url)))
					.apiKey(apiKey).apiSecret(apiSecret).callback(callback).build();

			final Token accessToken = new Token(prefs.accessToken()
					.get(), prefs.accessSecret().get());
			final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
					getString(R.string.ravelry_url) + "/projects/Jillda/list.json",
					prefs.username().get()));
			service.signRequest(accessToken, request);
			request.setConnectTimeout(10, TimeUnit.SECONDS);
			final Response response = request.send();
			try {
				return new Gson().fromJson(response.getBody(), ProjectResult.class);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				throw e;
			}
		}
	}

	class ProjectsListener implements RequestListener<ProjectResult> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			System.out.println("FAIL");
		}

		@Override
		public void onRequestSuccess(ProjectResult result) {
			System.out.println(result);
		}
	}
}