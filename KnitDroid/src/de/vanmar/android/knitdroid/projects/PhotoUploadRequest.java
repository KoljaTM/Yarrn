package de.vanmar.android.knitdroid.projects;

import android.content.Context;
import android.net.Uri;

import com.androidquery.util.AQUtility;

import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryRequest;
import de.vanmar.android.knitdroid.ravelry.RavelryException;
import de.vanmar.android.knitdroid.ravelry.dts.UploadResult;
import de.vanmar.android.knitdroid.util.UiHelper;
import de.vanmar.android.knitdroid.util.UiHelper_;

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
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("upload_token", token);
            parts.add("access_key", context.getString(R.string.api_key));
            inputStream = context.getContentResolver().openInputStream(photoUri);
            parts.add("file0", new FileSystemResource(photoUri.getPath()));

            MultiValueMap<String, String> headers = new HttpHeaders();
            // headers.add("Accept", "application/json");
            headers.add("Content-Type", "multipart/form-data");
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.getMessageConverters().add(0, new FormHttpMessageConverter());

            UploadResult uploadResult = restTemplate.postForObject(context.getString(R.string.ravelry_url)
                    + "/upload/image.json", requestEntity, UploadResult.class);
            imageId = uploadResult.uploads.get("file0").get("image_id");
            String result = addPhotoToProject(imageId, projectId);

            return result;
        } catch (final FileNotFoundException e) {
            onError(e);
            throw e;
        } finally {
            stopProgressDialog();
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
