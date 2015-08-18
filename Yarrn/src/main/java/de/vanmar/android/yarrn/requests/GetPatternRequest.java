package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.GsonBuilder;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.PatternResult;

public class GetPatternRequest extends AbstractRavelryGetRequest<PatternResult> {

    private final int patternId;

    public GetPatternRequest(Application application, YarrnPrefs_ prefs, int patternId) {
        super(PatternResult.class, application, prefs);
        this.patternId = patternId;
    }

    protected PatternResult parseResult(String responseBody) {
        return new GsonBuilder().setDateFormat("yyyy/MM/dd").create().fromJson(responseBody, PatternResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/patterns/%s.json", patternId));
    }
}