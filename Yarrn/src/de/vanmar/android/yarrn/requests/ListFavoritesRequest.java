package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.FavoritesResult;

public class ListFavoritesRequest extends AbstractRavelryGetRequest<FavoritesResult> {

    private int page;
    private int pageSize;
    private String searchQuery;
    private SearchOption searchOption;

    public enum SearchOption {
        TAGS, ALL
    }

    public ListFavoritesRequest(Application application, YarrnPrefs_ prefs, int page, int pageSize, String searchQuery, SearchOption searchOption) {
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
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/people/%s/favorites/list.json?types=project%%20pattern&page=%s&page_size=%s&%s",
                prefs.username().get(), page, pageSize, searchParam));
    }
}