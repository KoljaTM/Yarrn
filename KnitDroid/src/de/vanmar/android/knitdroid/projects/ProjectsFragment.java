package de.vanmar.android.knitdroid.projects;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import com.androidquery.util.AQUtility;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
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
import de.vanmar.android.knitdroid.ravelry.dts.Project;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectsResult;
import de.vanmar.android.knitdroid.util.ProjectsAdapter;
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

	private ProjectsAdapter adapter;

	private ProjectsFragmentListener listener;

	@AfterViews
	public void afterViews() {
		adapter = new ProjectsAdapter(getActivity()) {

			@Override
			protected void onProjectClicked(final Project project) {
				listener.onProjectSelected(project.id);
			}

		};
		projectlist.setAdapter(adapter);
	}

	@UiThread
	protected void displayProjects(final ProjectsResult result) {
		adapter.clear();
		adapter.addAll(result.projects);
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

		spiceManager.execute(new ProjectsRequest(), new ProjectsListener());
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

	class ProjectsRequest extends SpiceRequest<ProjectsResult> {

		public ProjectsRequest() {
			super(ProjectsResult.class);
		}

		@Override
		public ProjectsResult loadDataFromNetwork() throws Exception {
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
			return new Gson().fromJson(response.getBody(), ProjectsResult.class);
		}
	}

	class ProjectsListener implements RequestListener<ProjectsResult> {

		@Override
		public void onRequestFailure(SpiceException spiceException) {
			AQUtility.report(spiceException);
		}

		@Override
		public void onRequestSuccess(ProjectsResult result) {
			displayProjects(result);
		}
	}
}