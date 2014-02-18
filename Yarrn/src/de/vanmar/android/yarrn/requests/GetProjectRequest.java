package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.GsonBuilder;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.ProjectResult;

public class GetProjectRequest extends AbstractRavelryGetRequest<ProjectResult> {

    private final int projectId;
    private String username;

    public GetProjectRequest(Application application, YarrnPrefs_ prefs, int projectId, String username) {
        super(ProjectResult.class, application, prefs);
        this.projectId = projectId;
        this.username = username;
    }

    protected ProjectResult parseResult(String responseBody) {
        return new GsonBuilder().setDateFormat("yyyy/MM/dd").create().fromJson(responseBody, ProjectResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/projects/%s/%s.json", username, projectId));
    }
}