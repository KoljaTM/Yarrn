package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * http://www.ravelry.com/api#stash_list
 */
public class StashesResult implements ETaggable {

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
}
