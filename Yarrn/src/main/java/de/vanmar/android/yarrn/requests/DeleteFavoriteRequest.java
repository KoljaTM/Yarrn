package de.vanmar.android.yarrn.requests;

import android.app.Application;

import com.google.gson.Gson;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.ravelry.dts.BookmarkShort;

public class DeleteFavoriteRequest extends AbstractRavelryRequest<BookmarkShort> {
    private String favoriteId;

    public DeleteFavoriteRequest(YarrnPrefs_ prefs, Application application, String favoriteId) {
        super(BookmarkShort.class, prefs, application);
        this.favoriteId = favoriteId;
    }

    @Override
    public BookmarkShort loadDataFromNetwork() throws Exception {
        final OAuthRequest request = new OAuthRequest(Verb.DELETE,
                application.getString(R.string.ravelry_url)
                        + String.format("/people/%s/favorites/%s.json",
                        prefs.username().get(), favoriteId)
        );
        Response response = executeRequest(request);
        return new Gson().fromJson(response.getBody(), BookmarkShort.class);
    }
}