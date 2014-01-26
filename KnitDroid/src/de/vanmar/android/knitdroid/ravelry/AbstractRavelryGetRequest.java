package de.vanmar.android.knitdroid.ravelry;

import android.app.Application;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;

public abstract class AbstractRavelryGetRequest<T> extends AbstractRavelryRequest<T> {

    public AbstractRavelryGetRequest(Class<T> clazz, Application application, KnitdroidPrefs_ prefs) {
        super(clazz, prefs, application);
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        final OAuthRequest request = getRequest();
        final Response response = executeRequest(request);
        return parseResult(response.getBody());
    }


    protected abstract T parseResult(String responseBody);

    protected abstract OAuthRequest getRequest();

    public Object getCacheKey() {
        OAuthRequest request = getRequest();
        return request.getCompleteUrl() + "#" + request.getQueryStringParams().asFormUrlEncodedString();
    }

}
