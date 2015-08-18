package de.vanmar.android.yarrn;

import java.util.Collection;

/**
 * Created by Kolja on 02.03.14.
 */
public interface YarrnAdapter<T> {
    void addAllItems(Collection<? extends T> collection);

    void clear();
}