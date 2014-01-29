package de.vanmar.android.knitdroid.favorites;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.dts.FavoritesResult;

public class ListFavoritesRequest extends AbstractRavelryGetRequest<FavoritesResult> {

    public ListFavoritesRequest(Application application, KnitdroidPrefs_ prefs) {
        super(FavoritesResult.class, application, prefs);
    }

    protected FavoritesResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, FavoritesResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/people/%s/favorites/list.json?types=project%%20pattern",
                prefs.username().get()));
    }
}