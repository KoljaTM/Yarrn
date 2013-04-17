package de.vanmar.android.knitdroid.projects;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;

@EFragment(R.layout.fragment_projects)
public class ProjectsFragment extends Fragment {

	public interface ProjectsFragmentListener extends IRavelryActivity {
		/** Project with projectId was selected, 0 if no project selected */
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
	}
}