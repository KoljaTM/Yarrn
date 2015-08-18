package de.vanmar.android.yarrn.ravelry.dts;

import java.util.List;

/**
 * Created by Kolja on 23.02.14.
 */
public interface Paging<ITEM> extends ETaggable {
    Paginator getPaginator();

    List<ITEM> getItems();
}
