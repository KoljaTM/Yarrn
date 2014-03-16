package de.vanmar.android.yarrn.ravelry.dts;

import java.util.Collections;
import java.util.List;

/**
 * http://www.ravelry.com/api#patterns_search
 */
public class PatternsResult implements Paging<PatternShort> {

    public List<PatternShort> patterns;
    public Paginator paginator;
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
    public List<PatternShort> getItems() {
        return patterns;
    }

    public static PatternsResult emptyResult() {
        PatternsResult patternsResult = new PatternsResult();
        patternsResult.paginator = Paginator.emptyPaginator();
        patternsResult.patterns = Collections.emptyList();
        return patternsResult;
    }
}
