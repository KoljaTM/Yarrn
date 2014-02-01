package de.vanmar.android.knitdroid.ravelry.dts;

import java.util.List;

/**
 * http://www.ravelry.com/api#favorites_list
 */
public class FavoritesResult implements ETaggable {

    public Paginator paginator;
    public List<BookmarkShort> favorites;
    private String etag;

    @Override
    public void setETag(String etag) {
        this.etag = etag;
    }

    @Override
    public String getETag() {
        return this.etag;
    }
}
