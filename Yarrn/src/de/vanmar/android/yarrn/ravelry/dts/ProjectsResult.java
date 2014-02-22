package de.vanmar.android.yarrn.ravelry.dts;

import java.util.List;

/**
 * http://www.ravelry.com/api#projects_list
 */
public class ProjectsResult implements ETaggable {

    public Paginator paginator;
    public List<ProjectShort> projects;
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
