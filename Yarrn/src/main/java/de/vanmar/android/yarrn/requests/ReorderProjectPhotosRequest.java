package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.List;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.Photo;
import de.vanmar.android.yarrn.ravelry.dts.PhotoResult;

public class ReorderProjectPhotosRequest extends AbstractRavelryRequest<PhotoResult> {

    private int projectId;
    private List<Photo> photos;

    public ReorderProjectPhotosRequest(YarrnPrefs_ prefs, Application application, int projectId, List<Photo> photos) {
        super(PhotoResult.class, prefs, application);
        this.projectId = projectId;
        this.photos = photos;
    }

    public int getProjectId() {
        return projectId;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    @Override
    public PhotoResult loadDataFromNetwork() throws Exception {
        final OAuthRequest request = new OAuthRequest(Verb.POST,
                application.getString(R.string.ravelry_url)
                        + String.format("/projects/%s/%s/reorder_photos.json",
                        prefs.username().get(), projectId));
        request.addBodyParameter("sort_order", getSortOrder());
        Response response = executeRequest(request);
        return new Gson().fromJson(response.getBody(), PhotoResult.class);

    }

    private String getSortOrder() {
        StringBuilder photoIds = new StringBuilder();
        for (int i = 0; i < photos.size(); i++) {
            if (i > 0) {
                photoIds.append(" ");
            }
            photoIds.append(photos.get(i).id);
        }
        return photoIds.toString();
    }
}