package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * http://www.ravelry.com/api#queue_list
 */
public class QueuesResult implements Paging<QueuedProjectSmall> {

    public Paginator paginator;
    @SerializedName("queued_projects")
    public List<QueuedProjectSmall> queuedProjects;
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
    public List<QueuedProjectSmall> getItems() {
        return queuedProjects;
    }

    public static QueuesResult emptyResult() {
        QueuesResult projectsResult = new QueuesResult();
        projectsResult.paginator = Paginator.emptyPaginator();
        projectsResult.queuedProjects = Collections.emptyList();
        return projectsResult;
    }
}
