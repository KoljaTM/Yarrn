package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.GsonBuilder;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.StashResult;

public class GetStashRequest extends AbstractRavelryGetRequest<StashResult> {

    private final int stashId;
    private String username;

    public GetStashRequest(Application application, YarrnPrefs_ prefs, int stashId, String username) {
        super(StashResult.class, application, prefs);
        this.stashId = stashId;
        this.username = username;
    }

    protected StashResult parseResult(String responseBody) {
        return new GsonBuilder().setDateFormat("yyyy/MM/dd").create().fromJson(responseBody, StashResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/people/%s/stash/%s.json", username, stashId));
    }
}