package de.vanmar.android.knitdroid.ravelry.dts;

/**
 * http://www.ravelry.com/api#patterns_show
 */
public class PatternResult implements ETaggable {

    public Pattern pattern;
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
