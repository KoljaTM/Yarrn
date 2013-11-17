package de.vanmar.android.knitdroid.projects;

import android.content.Context;
import com.google.gson.Gson;
import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

public class GetProjectRequest extends AbstractRavelryGetRequest<ProjectResult> {

	private final int projectId;

	public GetProjectRequest(Context context, KnitdroidPrefs_ prefs, int projectId) {
		super(ProjectResult.class, context, prefs);
		this.projectId = projectId;
	}

	protected ProjectResult parseResult(String responseBody) {
		return new Gson().fromJson(responseBody, ProjectResult.class);
	}

	protected OAuthRequest getRequest() {
		return new OAuthRequest(Verb.GET, String.format(
				context.getString(R.string.ravelry_url) + "/projects/%s/%s.json", prefs
				.username().get(), projectId));
	}
}