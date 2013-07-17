package de.vanmar.android.knitdroid.projects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.meetme.android.horizontallistview.HorizontalListView;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.util.JSONHelper;

@EFragment(R.layout.fragment_project_detail)
public class ProjectFragment extends Fragment {

	public interface ProjectFragmentListener extends IRavelryActivity {

		void takePhoto();

		void pickImage();
	}

	@ViewById(R.id.name)
	TextView name;

	@ViewById(R.id.pattern_name)
	TextView patternName;

	@ViewById(R.id.status)
	TextView status;

	@ViewById(R.id.gallery)
	HorizontalListView gallery;

	private ProjectFragmentListener listener;

	private PhotoAdapter adapter;

	@Pref
	KnitdroidPrefs_ prefs;

	@AfterViews
	public void afterViews() {
		adapter = new PhotoAdapter(getActivity());
		gallery.setAdapter(adapter);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ProjectFragmentListener) {
			listener = (ProjectFragmentListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement ProjectFragmentListener");
		}
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

	@Background
	public void getProject(final int projectId,
			final ResultCallback<String> callback) {
		final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
				getString(R.string.ravelry_url) + "/projects/%s/%s.json", prefs
						.username().get(), projectId));
		listener.callRavelry(request, callback);
	}

	@UiThread
	protected void clearProject() {
		name.setText(null);
		patternName.setText(null);
		status.setText(null);

		adapter.setData(null);
	}

	@UiThread
	protected void displayProject(final String result) {
		try {
			final JSONObject jsonProject = new JSONObject(result)
					.optJSONObject("project");
			name.setText(JSONHelper.optString(jsonProject, "name"));
			patternName.setText(JSONHelper.optString(jsonProject,
					"pattern_name"));
			status.setText(JSONHelper.optString(jsonProject, "status_name"));

			final JSONArray photos = jsonProject.getJSONArray("photos");
			adapter.setData(photos);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.addPhoto)
	public void onAddPhotoClicked() {
		listener.pickImage();
	}

	@Click(R.id.takePhoto)
	public void onTakePhotoClicked() {
		listener.takePhoto();
	}

}