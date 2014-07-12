package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.QueuesResult;

/**
 * Created by Kolja on 15.03.14.
 */
public class ListQueuesRequest extends AbstractRavelryGetRequest<QueuesResult> {

    private int page;
    private int pageSize;

    public ListQueuesRequest(Application application, YarrnPrefs_ prefs, int page, int pageSize) {
        super(QueuesResult.class, application, prefs);
        this.page = page;
        this.pageSize = pageSize;
    }

    protected QueuesResult parseResult(String responseBody) {
        return new Gson().fromJson(responseBody, QueuesResult.class);
    }

    @Override
    public QueuesResult loadDataFromNetwork() throws Exception {
        return super.loadDataFromNetwork();
    }

    protected OAuthRequest getRequest() {
        return new OAuthRequest(Verb.GET,
                application.getString(R.string.ravelry_url) + String.format("/people/%s/queue/list.json?page=%s&page_size=%s",
                        prefs.username().get(), page, pageSize)
        );
    }
}