package de.vanmar.android.knitdroid.projects;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.meetme.android.horizontallistview.HorizontalListView;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;

@EFragment(R.layout.fragment_project_detail)
public class ProjectFragment extends Fragment {

	public interface ProjectFragmentListener extends IRavelryActivity {
	}

	private static final int REQUEST_CODE_PHOTO = 1;

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

	@AfterViews
	public void afterViews() {
		adapter = new PhotoAdapter(getActivity());
		gallery.setAdapter(adapter);
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
			name.setText(jsonProject.optString("name"));
			patternName.setText(jsonProject.optString("pattern_name"));
			status.setText(jsonProject.optString("status_name"));

			final JSONArray photos = jsonProject.getJSONArray("photos");
			adapter.setData(photos);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Background
	public void getProject(final int projectId,
			final ResultCallback<String> callback) {
		final OAuthRequest request = new OAuthRequest(Verb.GET, String.format(
				getString(R.string.ravelry_url) + "/projects/%s/%s.json",
				"Jillda", projectId));
		listener.callRavelry(request, callback);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		if (requestCode == REQUEST_CODE_PHOTO
				&& resultCode == Activity.RESULT_OK) {
			try {
				final InputStream stream = getActivity().getContentResolver()
						.openInputStream(data.getData());

				final OAuthRequest request = new OAuthRequest(Verb.POST,
						getString(R.string.ravelry_url)
								+ "/upload/request_token.json");
				listener.callRavelry(request, new ResultCallback<String>() {

					@Override
					public void onFailure(final Exception exception) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(final String result) {
						onTokenReceived(result, stream);
					}
				});
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Click(R.id.addPhoto)
	public void onAddPhotoClicked() {
		// pickImage();
		addPhotoToProject(19823521);
	}

	protected void addPhotoToProject(final int imageId) {
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				getString(R.string.ravelry_url)
						+ "/projects/KoljaTM/10406235/create_photo.json");
		request.addBodyParameter("image_id", String.valueOf(imageId));
		listener.callRavelry(request, new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				exception.printStackTrace();
			}

			@Override
			public void onSuccess(final String result) {
				System.err.println(result);
			}
		});
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

	protected void onTokenReceived(final String result, final InputStream stream) {
		System.out.println(result);
		try {
			final String token = new JSONObject(result)
					.optString("upload_token");
			final AQuery aq = new AQuery(getActivity());

			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("upload_token", token);
			params.put("access_key", getActivity().getString(R.string.api_key));
			params.put("file0", stream);

			aq.ajax(getActivity().getString(R.string.ravelry_url)
					+ "/upload/image.json", params, JSONObject.class,
					new AjaxCallback<JSONObject>() {

						@Override
						public void callback(final String url,
								final JSONObject object, final AjaxStatus status) {
							System.out.println(object);
						}
					});
		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void pickImage() {
		final Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_PHOTO);
	}

}