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

    private int page;
    private int pageSize;

    public ListFavoritesRequest(Application application, KnitdroidPrefs_ prefs, int page, int pageSize) {
        super(FavoritesResult.class, application, prefs);
        this.page = page;
        this.pageSize = pageSize;
    }

    protected FavoritesResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, FavoritesResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/people/%s/favorites/list.json?types=project%%20pattern&page=%s&page_size=%s",
                prefs.username().get(), page, pageSize));
    }
}