package de.vanmar.android.yarrn.requests;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.SearchCriteria;
import de.vanmar.android.yarrn.ravelry.dts.StashSearchResult;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchStashesRequest extends AbstractRavelryGetRequest<StashSearchResult> {

    private List<SearchCriteria> searchCriteriaList;
    private int page;
    private int pageSize;

    public SearchStashesRequest(Application application, YarrnPrefs_ prefs, List<SearchCriteria> searchCriteriaList, int page, int pageSize) {
        super(StashSearchResult.class, application, prefs);
        this.searchCriteriaList = searchCriteriaList;
        this.page = page;
        this.pageSize = pageSize;
    }

    protected StashSearchResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, StashSearchResult.class);
    }

    @Override
    public StashSearchResult loadDataFromNetwork() throws Exception {
        if (searchCriteriaList.isEmpty()) {
            return StashSearchResult.emptyResult();
        }
        return super.loadDataFromNetwork();
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/stash/search.json?%spage=%s&page_size=%s",
                        getSearchCriteriaString(), page, pageSize)
        );
    }

    private String getSearchCriteriaString() {
        StringBuilder sb = new StringBuilder();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            try {
                sb.append(searchCriteria.getName()).append("=").append(URLEncoder.encode(searchCriteria.getValue(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                Log.e("SeachStashesRequest", e.getMessage(), e);
            }
        }
        return sb.toString();
    }
}