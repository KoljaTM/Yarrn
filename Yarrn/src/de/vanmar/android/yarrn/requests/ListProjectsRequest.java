package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;

public class ListProjectsRequest extends AbstractRavelryGetRequest<ProjectsResult> {

    public ListProjectsRequest(Application application, YarrnPrefs_ prefs) {
        super(ProjectsResult.class, application, prefs);
    }

    protected ProjectsResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, ProjectsResult.class);
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET, String.format(
                application.getString(R.string.ravelry_url) + "/projects/%s/list.json?sort=%s",
                prefs.username().get(), getSort()));
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