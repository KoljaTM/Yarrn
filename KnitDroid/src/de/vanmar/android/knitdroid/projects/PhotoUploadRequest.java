package de.vanmar.android.knitdroid.projects;

import android.app.Application;
import android.net.Uri;

import com.androidquery.util.AQUtility;

import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.RavelryException;
import de.vanmar.android.knitdroid.ravelry.dts.UploadResult;
import de.vanmar.android.knitdroid.requests.AbstractRavelryRequest;

public class PhotoUploadRequest extends AbstractRavelryRequest<String> {
    private final Uri photoUri;
    private final int projectId;

    public PhotoUploadRequest(Application application, KnitdroidPrefs_ prefs, Uri photoUri, int projectId) {
        super(String.class, prefs, application);
        this.photoUri = photoUri;
        this.projectId = projectId;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        final OAuthRequest request = new OAuthRequest(Verb.POST,
                application.getString(R.string.ravelry_url)
                        + "/upload/request_token.json");
        Response requestTokenResponse = executeRequest(request);

        final String token = new JSONObject(requestTokenResponse.getBody()).getString("upload_token");

        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
            parts.add("upload_token", token);
            parts.add("access_key", application.getString(R.string.api_key));
            final InputStream inputStream = application.getContentResolver().openInputStream(photoUri);
            parts.add("file0", new InputStreamResource(inputStream) {
                @Override
                public long contentLength() throws IOException {
                    return inputStream.available();
                }

                @Override
                public String getFilename() throws IllegalStateException {
                    return photoUri.getLastPathSegment();
                }
            });

            MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add("Content-Type", "multipart/form-data");
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
            RestTemplate restTemplate = getRestTemplate();
            restTemplate.getMessageConverters().add(0, new FormHttpMessageConverter());

            UploadResult uploadResult = restTemplate.postForObject(application.getString(R.string.ravelry_url)
                    + "/upload/image.json", requestEntity, UploadResult.class);
            Integer imageId = uploadResult.get("uploads").get("file0").get("image_id");

            return addPhotoToProject(imageId, projectId);
        } catch (final FileNotFoundException e) {
            onError(e);
            throw e;
        }
    }

    private String addPhotoToProject(final int imageId, final int projectId) throws RavelryException {
        final OAuthRequest request = new OAuthRequest(Verb.POST,
                application.getString(R.string.ravelry_url)
                        + String.format("/projects/%s/%s/create_photo.json",
                        prefs.username().get(), projectId));
        request.addBodyParameter("image_id", String.valueOf(imageId));
        Response response = executeRequest(request);
        return response.getBody();
    }

    private void onError(final Exception exception) {
        AQUtility.report(exception);
    }
}
