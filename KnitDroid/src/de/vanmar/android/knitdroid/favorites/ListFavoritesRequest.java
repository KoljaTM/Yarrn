package de.vanmar.android.knitdroid.favorites;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.dts.FavoritesResult;

public class ListFavoritesRequest extends AbstractRavelryGetRequest<FavoritesResult> {

    private int page;
    private int pageSize;
    private String searchQuery;
    private SearchOption searchOption;

    public enum SearchOption {
        TAGS, ALL
    }

    public ListFavoritesRequest(Application application, KnitdroidPrefs_ prefs, int page, int pageSize, String searchQuery, SearchOption searchOption) {
        super(FavoritesResult.class, application, prefs);
        this.page = page;
        this.pageSize = pageSize;
        try {
            this.searchQuery = URLEncoder.encode(searchQuery, "utf-8");
        } catch (UnsupportedEncodingException e) {
            this.searchQuery = "";
        }
        this.searchOption = searchOption;
    }

    protected FavoritesResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, FavoritesResult.class);
    }

    protected OAuthRequest getRequest() {
        String searchParam = "";
        if (searchOption != null) {
            switch (searchOption) {
                case TAGS:
                    searchParam = "tag=" + searchQuery;
                    break;
                case ALL:
                    searchParam = "deep_search=true&query=" + searchQuery;
                    break;
            }
        }
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/people/%s/favorites/list.json?types=project%%20pattern&page=%s&page_size=%s&%s",
                prefs.username().get(), page, pageSize, searchParam));
    }
}