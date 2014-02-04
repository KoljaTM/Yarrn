package de.vanmar.android.knitdroid.requests;

import android.app.Application;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;

public class UpdateProjectRequest extends AbstractRavelryRequest<ProjectResult> {


    private int projectId;
    private JsonObject updateData;

    public UpdateProjectRequest(KnitdroidPrefs_ prefs, Application application, int projectId, JsonObject updateData) {
        super(ProjectResult.class, prefs, application);
        this.projectId = projectId;
        this.updateData = updateData;
    }

    public int getProjectId() {
        return projectId;
    }

    public JsonObject getUpdateData() {
        return updateData;
    }

    @Override
    public ProjectResult loadDataFromNetwork() throws Exception {
        final OAuthRequest request = new OAuthRequest(Verb.POST,
                application.getString(R.string.ravelry_url)
                        + String.format("/projects/%s/%s.json",
                        prefs.username().get(), projectId));
        request.addBodyParameter("data", updateData.toString());
        Response response = executeRequest(request);
        return new GsonBuilder().setDateFormat("yyyy/MM/dd").create().fromJson(response.getBody(), ProjectResult.class);

    }
}