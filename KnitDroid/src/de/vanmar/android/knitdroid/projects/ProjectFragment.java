package de.vanmar.android.knitdroid.projects;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.MainActivity;
import de.vanmar.android.knitdroid.R;
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

	@AfterViews
	public void afterViews() {
		jsonAdapter = new JSONAdapter() {

			@Override
			public View getView(final int position, final View convertView,
					final ViewGroup parent) {
				final View view;
				// TODO: ViewHolder
				if (convertView != null) {
					view = convertView;
				} else {
					view = getActivity().getLayoutInflater().inflate(
							R.layout.projectlist_item, parent, false);
				}
				final JSONObject jsonObject = getObject(position);
				((TextView) view.findViewById(R.id.name)).setText(jsonObject
						.optString("name"));
				Log.w("JSONAdapter", jsonObject.optJSONObject("first_photo")
						.optString("thumbnail_url"));
				final AQuery aq = new AQuery(view);
				aq.id(R.id.thumb).image(
						jsonObject.optJSONObject("first_photo").optString(
								"thumbnail_url"));
				return view;
			}
		};
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
				"https://api.ravelry.com/projects/%s/list.json", prefs
						.username().get()));
		((MainActivity) getActivity()).callRavelry(request, callback);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		getProjects(new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				exception.printStackTrace();
			}

			@Override
			public void onSuccess(final String result) {
				displayProjects(result);
			}
		});
	}
}
