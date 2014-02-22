package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.StashesResult;

public class ListStashesRequest extends AbstractRavelryGetRequest<StashesResult> {

    public ListStashesRequest(Application application, YarrnPrefs_ prefs) {
        super(StashesResult.class, application, prefs);
    }

    protected StashesResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, StashesResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/people/%s/stash/list.json?sort=%s",
                prefs.username().get(), getSort()));
    }

    private String getSort() {
        String sortParam = application.getResources().getStringArray(R.array.stash_sort_option_values)[prefs.stashSort().get()];
        if (prefs.stashSortReverse().get()) {
            return sortParam + "_";
        } else {
            return sortParam;
        }
    }
}