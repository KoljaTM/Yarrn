package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#queue_show
 */
public class QueuedProjectResult implements ETaggable {

    @SerializedName("queued_project")
    public QueuedProject queuedProject;
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
