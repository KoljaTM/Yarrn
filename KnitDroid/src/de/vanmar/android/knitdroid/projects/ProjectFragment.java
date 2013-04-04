package de.vanmar.android.knitdroid.projects;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.TextView;

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
import de.vanmar.android.knitdroid.util.JSONAdapter;

@EFragment(R.layout.fragment_projects)
public class ProjectFragment extends Fragment {

	@ViewById(R.id.hello)
	TextView hello;

	@ViewById(R.id.projectlist)
	ListView projectlist;

	@Pref
	KnitdroidPrefs_ prefs;

	private JSONAdapter jsonAdapter;

	private IRavelryActivity listener;

	@AfterViews
	public void afterViews() {
		jsonAdapter = new ProjectListAdapter(getActivity());
		projectlist.setAdapter(jsonAdapter);
	}

	@UiThread
	protected void displayProjects(final String result) {
		try {
			final JSONObject json = new JSONObject(result);
			jsonAdapter.setData(json.getJSONArray("projects"));
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Background
	public void getProjects(final ResultCallback<String> callback) {
		final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
				"https://api.ravelry.com/projects/%s/list.json", "Jillda"));
		listener.callRavelry(request, callback);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IRavelryActivity) {
			listener = (IRavelryActivity) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet IRavelryActivity");
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