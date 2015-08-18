package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * http://www.ravelry.com/api#stash_list
 */
public class StashesResult implements Paging<StashShort> {

    public Paginator paginator;
    @SerializedName("stash")
    public List<StashShort> stashes;
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
    public List<StashShort> getItems() {
        return stashes;
    }

    public static StashesResult emptyResult() {
        StashesResult stashesResult = new StashesResult();
        stashesResult.paginator = Paginator.emptyPaginator();
        stashesResult.stashes = Collections.emptyList();
        return stashesResult;
    }
}
