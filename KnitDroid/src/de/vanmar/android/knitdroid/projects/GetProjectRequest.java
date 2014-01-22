package de.vanmar.android.knitdroid.projects;

import android.content.Context;

import com.google.gson.GsonBuilder;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;

public class GetProjectRequest extends AbstractRavelryGetRequest<ProjectResult> {

	private final int projectId;

	public GetProjectRequest(Context context, KnitdroidPrefs_ prefs, int projectId) {
		super(ProjectResult.class, context, prefs);
		this.projectId = projectId;
	}

	protected ProjectResult parseResult(String responseBody) {
        return new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss Z").create().fromJson(responseBody, ProjectResult.class);
    }

	protected OAuthRequest getRequest() {
		return new OAuthRequest(Verb.GET, String.format(
				context.getString(R.string.ravelry_url) + "/projects/%s/%s.json", prefs
				.username().get(), projectId));
	}
}