package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;

public class ListProjectsRequest extends AbstractRavelryGetRequest<ProjectsResult> {

    private int page;
    private int pageSize;

    public ListProjectsRequest(Application application, YarrnPrefs_ prefs, int page, int pageSize) {
        super(ProjectsResult.class, application, prefs);
        this.page = page;
        this.pageSize = pageSize;
    }

    protected ProjectsResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, ProjectsResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/projects/%s/list.json?sort=%s&page=%s&page_size=%s",
                prefs.username().get(), getSort(), page, pageSize));
    }

    private String getSort() {
        String sortParam = application.getResources().getStringArray(R.array.project_sort_option_values)[prefs.projectSort().get()];
        if (prefs.projectSortReverse().get()) {
            return sortParam + "_";
        } else {
            return sortParam;
        }
    }
}