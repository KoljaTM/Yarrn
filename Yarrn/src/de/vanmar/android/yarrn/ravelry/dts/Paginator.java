package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kolja on 01.02.14.
 */
public class Paginator {

    @SerializedName("last_page")
    public int lastPage;
    @SerializedName("page")
    public int page;
    @SerializedName("results")
    public int results;
    @SerializedName("page_count")
    public int pageCount;
    @SerializedName("page_size")
    public int pageSize;

    public static Paginator emptyPaginator() {
        Paginator paginator = new Paginator();
        paginator.lastPage = 1;
        paginator.page = 1;
        paginator.results = 0;
        paginator.pageCount = 1;
        paginator.pageSize = 1;
        return paginator;
    }
}
