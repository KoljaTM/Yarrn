package de.vanmar.android.knitdroid.projects;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.projects.ProjectsFragment.ProjectsFragmentListener;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;

@EFragment(R.layout.fragment_project_detail)
public class ProjectFragment extends Fragment {

	public interface ProjectFragmentListener extends IRavelryActivity {
	}

	@ViewById(R.id.name)
	TextView name;

	@ViewById(R.id.pattern_name)
	TextView patternName;

	@ViewById(R.id.status)
	TextView status;

	private ProjectsFragmentListener listener;

	@UiThread
	protected void displayProject(final String result) {
		try {
			final JSONObject jsonProject = new JSONObject(result)
					.optJSONObject("project");
			name.setText(jsonProject.optString("name"));
			patternName.setText(jsonProject.optString("pattern_name"));
			status.setText(jsonProject.optString("status_name"));
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@UiThread
	protected void clearProject() {
		name.setText(null);
		patternName.setText(null);
		status.setText(null);
	}

	@Background
	public void getProject(final int projectId,
			final ResultCallback<String> callback) {
		final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
				"https://api.ravelry.com/projects/%s/%s.json", "Jillda",
				projectId));
		listener.callRavelry(request, callback);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ProjectsFragmentListener) {
			listener = (ProjectsFragmentListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet ProjectsFragmentListener");
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

	public void onProjectSelected(final int projectId) {
		if (projectId == 0) {
			clearProject();
		} else {

			getProject(projectId, new ResultCallback<String>() {

				@Override
				public void onFailure(final Exception exception) {
					AQUtility.report(exception);
				}

				@Override
				public void onSuccess(final String result) {
					displayProject(result);
				}
			});
		}
	}
}