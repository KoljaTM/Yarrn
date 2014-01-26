package de.vanmar.android.knitdroid.ravelry;

import android.app.Application;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.persistence.springandroid.json.gson.GsonObjectPersisterFactory;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.ravelry.dts.ETaggable;

public abstract class AbstractRavelryGetRequest<T extends ETaggable> extends AbstractRavelryRequest<T> {

    public static final long CACHE_DURATION = DurationInMillis.ONE_MINUTE;
    private final GsonObjectPersisterFactory persisterFactory;


    public AbstractRavelryGetRequest(Class<T> clazz, Application application, KnitdroidPrefs_ prefs) {
        super(clazz, prefs, application);
        try {
            persisterFactory = new GsonObjectPersisterFactory(application);
        } catch (SpiceException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public T loadDataFromNetwork() throws Exception {
        final OAuthRequest request = getRequest();
        T dataFromCache = persisterFactory.createObjectPersister(getResultType()).loadDataFromCache(getCacheKey(), DurationInMillis.ALWAYS_RETURNED);
        if (dataFromCache != null) {
            request.addHeader("If-None-Match", dataFromCache.getETag());
        }
        final Response response = executeRequest(request);
        if (response.getCode() == 304) {
            return dataFromCache;
        } else {
            T result = parseResult(response.getBody());
            result.setETag(response.getHeader("ETag"));
            return result;
        }
    }


    protected abstract T parseResult(String responseBody);

    protected abstract OAuthRequest getRequest();

    public Object getCacheKey() {
        OAuthRequest request = getRequest();
        return request.getCompleteUrl() + "#" + request.getQueryStringParams().asFormUrlEncodedString();
    }

}
