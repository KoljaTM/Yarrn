package de.vanmar.android.yarrn.ravelry.dts;

import java.util.Collections;
import java.util.List;

/**
 * http://www.ravelry.com/api#projects_list
 */
public class ProjectsResult implements Paging<ProjectShort> {

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

    @Override
    public Paginator getPaginator() {
        return paginator;
    }

    @Override
    public List<ProjectShort> getItems() {
        return projects;
    }

    public static ProjectsResult emptyResult() {
        ProjectsResult projectsResult = new ProjectsResult();
        projectsResult.paginator = Paginator.emptyPaginator();
        projectsResult.projects = Collections.emptyList();
        return projectsResult;
    }
}
