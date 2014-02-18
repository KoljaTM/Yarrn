package de.vanmar.android.yarrn.ravelry.dts;

/**
 * Created by Kolja on 26.01.14.
 */
public interface ETaggable {
    void setETag(String etag);

    String getETag();
}
