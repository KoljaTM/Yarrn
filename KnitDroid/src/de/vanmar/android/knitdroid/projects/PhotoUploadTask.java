package de.vanmar.android.knitdroid.projects;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import android.app.ProgressDialog;
import android.net.Uri;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.ResultCallback;
import de.vanmar.android.knitdroid.util.JSONHelper;

@EBean
public class PhotoUploadTask {

	@RootContext
	ProjectDetailActivity activity;

	@Pref
	KnitdroidPrefs_ prefs;

	private ProgressDialog progressDialog;

	public void uploadPhotoToProject(final Uri photoUri, final int projectId) {
		startProgress();
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				activity.getString(R.string.ravelry_url)
						+ "/upload/request_token.json");
		activity.callRavelry(request, new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				onError(exception);
			}

			@Override
			public void onSuccess(final String result) {
				try {
					final String token = JSONHelper.optString(new JSONObject(
							result), "upload_token");
					onTokenReceived(token, photoUri, projectId);
				} catch (final JSONException e) {
					onError(e);
				}
			}
		});
	}

	private void onTokenReceived(final String token, final Uri uri,
			final int projectId) {
		InputStream inputStream = null;
		try {
			final AQuery aq = new AQuery(activity);

			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("upload_token", token);
			params.put("access_key", activity.getString(R.string.api_key));
			// TODO close stream?
			inputStream = activity.getContentResolver().openInputStream(uri);
			params.put("file0", inputStream);

			aq.ajax(activity.getString(R.string.ravelry_url)
					+ "/upload/image.json", params, JSONObject.class,
					new AjaxCallback<JSONObject>() {

						@Override
						public void callback(final String url,
								final JSONObject object, final AjaxStatus status) {

							int imageId;
							try {
								imageId = object.getJSONObject("uploads")
										.getJSONObject("file0")
										.getInt("image_id");
								addPhotoToProject(imageId, projectId);
							} catch (final JSONException e) {
								onError(e);
							}
						}

						@Override
						public void failure(final int code, final String message) {
							super.failure(code, message);
						}
					});
		} catch (final FileNotFoundException e) {
			onError(e);
		}
	}

	private void addPhotoToProject(final int imageId, final int projectId) {
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				activity.getString(R.string.ravelry_url)
						+ String.format("/projects/%s/%s/create_photo.json",
								prefs.username().get(), projectId));
		request.addBodyParameter("image_id", String.valueOf(imageId));
		activity.callRavelry(request, new ResultCallback<String>() {

			@Override
			public void onFailure(final Exception exception) {
				onError(exception);
			}

			@Override
			public void onSuccess(final String result) {
				activity.onPhotoUploadSuccess();
				stopProgressDialog();
			}
		});
	}

	private void onError(final Exception exception) {
		AQUtility.report(exception);
		stopProgressDialog();
	}

	private void startProgress() {
		progressDialog = ProgressDialog.show(activity,
				activity.getString(R.string.upload_progress_title),
				activity.getString(R.string.upload_progress_message), true,
				false);
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
