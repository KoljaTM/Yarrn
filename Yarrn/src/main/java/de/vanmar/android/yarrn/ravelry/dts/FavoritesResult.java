package de.vanmar.android.yarrn.ravelry.dts;

import java.util.List;

/**
 * http://www.ravelry.com/api#favorites_list
 */
public class FavoritesResult implements Paging<BookmarkShort> {

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

    @Override
    public Paginator getPaginator() {
        return paginator;
    }

    @Override
    public List<BookmarkShort> getItems() {
        return favorites;
    }
}
