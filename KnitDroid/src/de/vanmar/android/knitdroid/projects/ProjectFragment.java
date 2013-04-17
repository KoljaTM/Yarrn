package de.vanmar.android.knitdroid.projects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.meetme.android.horizontallistview.HorizontalListView;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;

@EFragment(R.layout.fragment_project_detail)
public class ProjectFragment extends Fragment {

	public interface ProjectFragmentListener extends IRavelryActivity {
	}

	private static final int REQUEST_CODE_GALLERY = 1;
	private static final int REQUEST_CODE_CAMERA = 2;

	private static final String JPEG_FILE_PREFIX = "IMG_";

	private static final String JPEG_FILE_SUFFIX = ".jpg";

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

	private int projectId = 0;

	@Pref
	KnitdroidPrefs_ prefs;

	private Uri photoUri;

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
		this.projectId = projectId;
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
			name.setText(jsonProject.optString("name"));
			patternName.setText(jsonProject.optString("pattern_name"));
			status.setText(jsonProject.optString("status_name"));

			final JSONArray photos = jsonProject.getJSONArray("photos");
			adapter.setData(photos);
		} catch (final JSONException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.addPhoto)
	public void onAddPhotoClicked() {
		pickImage();
	}

	@Click(R.id.takePhoto)
	public void onTakePhotoClicked() {
		takePhoto();
	}

	public void pickImage() {
		final Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_CODE_GALLERY);
	}

	@SuppressLint("SimpleDateFormat")
	private void takePhoto() {
		final File storageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Knitdroid");
		storageDir.mkdirs();

		final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		final String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File image;
		try {
			image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
					storageDir);
			final Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			photoUri = Uri.fromFile(image);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		if (requestCode == REQUEST_CODE_GALLERY
				&& resultCode == Activity.RESULT_OK) {
			onReceivedImage(data.getData());
		} else if (requestCode == REQUEST_CODE_CAMERA
				&& resultCode == Activity.RESULT_OK && photoUri != null) {
			onReceivedImage(photoUri);
			photoUri = null;
		}
	}

	private void onReceivedImage(final Uri data) {
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				getString(R.string.ravelry_url) + "/upload/request_token.json");
		listener.callRavelry(request, new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final String result) {
				onTokenReceived(result, data);
			}
		});
	}

	protected void onTokenReceived(final String result, final Uri uri) {
		System.out.println(result);
		InputStream inputStream = null;
		try {
			final String token = new JSONObject(result)
					.optString("upload_token");
			final AQuery aq = new AQuery(getActivity());

			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("upload_token", token);
			params.put("access_key", getActivity().getString(R.string.api_key));
			// TODO close stream?
			inputStream = getActivity().getContentResolver().openInputStream(
					uri);
			params.put("file0", inputStream);

			aq.ajax(getActivity().getString(R.string.ravelry_url)
					+ "/upload/image.json", params, JSONObject.class,
					new AjaxCallback<JSONObject>() {

						@Override
						public void callback(final String url,
								final JSONObject object, final AjaxStatus status) {
							System.out.println(object);
							int imageId;
							try {
								imageId = object.getJSONObject("uploads")
										.getJSONObject("file0")
										.getInt("image_id");
								addPhotoToProject(imageId);
							} catch (final JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void failure(final int code, final String message) {
							// TODO Auto-generated method stub
							super.failure(code, message);
						}
					});
		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void addPhotoToProject(final int imageId) {
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				getString(R.string.ravelry_url)
						+ String.format("/projects/%s/%s/create_photo.json",
								prefs.username().get(), projectId));
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

}