package de.vanmar.android.knitdroid.ravelry.dts;

/**
 * http://www.ravelry.com/api#projects_show
 */
public class ProjectResult implements ETaggable {

    public Project project;
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
