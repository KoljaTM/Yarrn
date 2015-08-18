package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.GsonBuilder;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.QueuedProjectResult;

public class GetQueuedProjectRequest extends AbstractRavelryGetRequest<QueuedProjectResult> {

    private final int projectId;
    private String username;

    public GetQueuedProjectRequest(Application application, YarrnPrefs_ prefs, int projectId, String username) {
        super(QueuedProjectResult.class, application, prefs);
        this.projectId = projectId;
        this.username = username;
    }

    protected QueuedProjectResult parseResult(String responseBody) {
        return new GsonBuilder().setDateFormat("yyyy/MM/dd").create().fromJson(responseBody, QueuedProjectResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/people/%s/queue/%s.json", username, projectId));
    }
}