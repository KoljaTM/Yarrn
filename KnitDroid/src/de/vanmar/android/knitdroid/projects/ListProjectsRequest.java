package de.vanmar.android.knitdroid.projects;

import android.content.Context;
import com.google.gson.Gson;
import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectsResult;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

public class ListProjectsRequest extends AbstractRavelryGetRequest<ProjectsResult> {

	public ListProjectsRequest(Context context, KnitdroidPrefs_ prefs) {
		super(ProjectsResult.class, context, prefs);
	}

	protected ProjectsResult parseResult(String responseBody) {
		return new Gson().fromJson(responseBody, ProjectsResult.class);
	}

	protected OAuthRequest getRequest() {
		return new OAuthRequest(Verb.GET, String.format(
				context.getString(R.string.ravelry_url) + "/projects/%s/list.json",
				prefs.username().get()));
	}
}