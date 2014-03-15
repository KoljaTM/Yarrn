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
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchProjectsRequest extends AbstractRavelryGetRequest<ProjectsResult> {

    private List<SearchCriteria> searchCriteriaList;
    private int page;
    private int pageSize;

    public SearchProjectsRequest(Application application, YarrnPrefs_ prefs, List<SearchCriteria> searchCriteriaList, int page, int pageSize) {
        super(ProjectsResult.class, application, prefs);
        this.searchCriteriaList = searchCriteriaList;
        this.page = page;
        this.pageSize = pageSize;
    }

    protected ProjectsResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, ProjectsResult.class);
    }

    @Override
    public ProjectsResult loadDataFromNetwork() throws Exception {
        if (searchCriteriaList.isEmpty()) {
            return ProjectsResult.emptyResult();
        }
        return super.loadDataFromNetwork();
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/projects/search.json?%spage=%s&page_size=%s",
                        getSearchCriteriaString(), page, pageSize)
        );
    }

    private String getSearchCriteriaString() {
        StringBuilder sb = new StringBuilder();
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            try {
                sb.append(searchCriteria.getName()).append("=").append(URLEncoder.encode(searchCriteria.getValue(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                Log.e("SeachProjectsRequest", e.getMessage(), e);
            }
        }
        return sb.toString();
    }
}