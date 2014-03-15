package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchProjectsRequest extends AbstractRavelryGetRequest<ProjectsResult> {

    private String query;
    private int page;
    private int pageSize;

    public SearchProjectsRequest(Application application, YarrnPrefs_ prefs, String query, int page, int pageSize) {
        super(ProjectsResult.class, application, prefs);
        this.query = query;
        this.page = page;
        this.pageSize = pageSize;
    }

    protected ProjectsResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, ProjectsResult.class);
    }

    @Override
    public ProjectsResult loadDataFromNetwork() throws Exception {
        if (StringUtils.isEmpty(query)) {
            return ProjectsResult.emptyResult();
        }
        return super.loadDataFromNetwork();
    }

    protected OAuthRequest getRequest() {
        try {
            return new OAuthRequest(Verb.GET,
                    application.getString(R.string.ravelry_url) + String.format("/projects/search.json?query=%s&page=%s&page_size=%s",
                            URLEncoder.encode(query, "utf-8"), page, pageSize)
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}