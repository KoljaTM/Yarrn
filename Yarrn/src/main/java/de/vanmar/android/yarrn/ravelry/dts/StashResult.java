package de.vanmar.android.yarrn.ravelry.dts;

/**
 * http://www.ravelry.com/api#stash_show
 */
public class StashResult implements ETaggable {

    public Stash stash;
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
