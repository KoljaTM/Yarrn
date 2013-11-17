package de.vanmar.android.knitdroid.projects;

import android.content.Context;
import android.net.Uri;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryRequest;
import de.vanmar.android.knitdroid.ravelry.RavelryException;
import de.vanmar.android.knitdroid.util.UiHelper;
import de.vanmar.android.knitdroid.util.UiHelper_;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PhotoUploadRequest extends AbstractRavelryRequest<String> {
	private final Uri photoUri;
	private final int projectId;
	private UiHelper uiHelper;
	private Integer imageId = null;
	private Exception exception = null;

	public PhotoUploadRequest(Context context, KnitdroidPrefs_ prefs, Uri photoUri, int projectId) {
		super(String.class, prefs, context);
		this.photoUri = photoUri;
		this.projectId = projectId;
		this.uiHelper = UiHelper_.getInstance_(context);
	}


	@Override
	public String loadDataFromNetwork() throws Exception {
		startProgress();
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				context.getString(R.string.ravelry_url)
						+ "/upload/request_token.json");
		Response requestTokenResponse = executeRequest(request);

		final String token = new JSONObject(requestTokenResponse.getBody()).getString("upload_token");

		InputStream inputStream;
		try {
			final AQuery aq = new AQuery(context);

			final Map<String, Object> params = new HashMap<String, Object>();
			params.put("upload_token", token);
			params.put("access_key", context.getString(R.string.api_key));

			inputStream = context.getContentResolver().openInputStream(photoUri);
			params.put("file0", inputStream);

			AjaxCallback<JSONObject> ajaxCallback = new AjaxCallback<JSONObject>() {

				@Override
				public void callback(final String url,
				                     final JSONObject object, final AjaxStatus status) {

					try {
						imageId = object.getJSONObject("uploads")
								.getJSONObject("file0")
								.getInt("image_id");
					} catch (final Exception e) {
						exception = e;
					}
				}

				@Override
				public void failure(final int code, final String message) {
					exception = new RavelryException(code);
				}
			};
			ajaxCallback.url(context.getString(R.string.ravelry_url)
					+ "/upload/image.json").params(params).type(JSONObject.class);
			aq.sync(ajaxCallback);
			if (exception != null) {
				throw exception;
			}
			String result = addPhotoToProject(imageId, projectId);
			stopProgressDialog();

			return result;
		} catch (final FileNotFoundException e) {
			onError(e);
			throw e;
		}
	}

	private String addPhotoToProject(final int imageId, final int projectId) throws RavelryException {
		final OAuthRequest request = new OAuthRequest(Verb.POST,
				context.getString(R.string.ravelry_url)
						+ String.format("/projects/%s/%s/create_photo.json",
						prefs.username().get(), projectId));
		request.addBodyParameter("image_id", String.valueOf(imageId));
		Response response = executeRequest(request);
		return response.getBody();
	}

	private void onError(final Exception exception) {
		AQUtility.report(exception);
		stopProgressDialog();
	}

	private void startProgress() {
		uiHelper.startProgress(context.getString(R.string.upload_progress_title),
				context.getString(R.string.upload_progress_message), true,
				false);
	}

	private void stopProgressDialog() {
		uiHelper.stopProgress();
	}

}
