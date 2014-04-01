package de.vanmar.android.yarrn.requests;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.SearchCriteria;
import de.vanmar.android.yarrn.ravelry.dts.PatternsResult;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchPatternsRequest extends AbstractRavelryGetRequest<PatternsResult> {

    private Collection<SearchCriteria> searchCriteriaList;
    private int page;
    private int pageSize;

    public SearchPatternsRequest(Application application, YarrnPrefs_ prefs, Collection<SearchCriteria> searchCriteriaList, int page, int pageSize) {
        super(PatternsResult.class, application, prefs);
        this.searchCriteriaList = searchCriteriaList;
        this.page = page;
        this.pageSize = pageSize;
    }

    protected PatternsResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, PatternsResult.class);
    }

    @Override
    public PatternsResult loadDataFromNetwork() throws Exception {
        if (searchCriteriaList.isEmpty()) {
            return PatternsResult.emptyResult();
        }
        return super.loadDataFromNetwork();
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/patterns/search.json?%spage=%s&page_size=%s",
                        getSearchCriteriaString(), page, pageSize)
        );
    }

    private String getSearchCriteriaString() {
        StringBuilder sb = new StringBuilder();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            try {
                sb.append(searchCriteria.getName()).append("=").append(URLEncoder.encode(searchCriteria.getValue(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                Log.e("SeachPatternsRequest", e.getMessage(), e);
            }
        }
        return sb.toString();
    }
}