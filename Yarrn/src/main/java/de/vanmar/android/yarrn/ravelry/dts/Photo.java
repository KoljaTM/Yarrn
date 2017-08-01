package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#Photo__result
 */
public class Photo {
    @SerializedName("id")
    public String id;
    @SerializedName("square_url")
    public String squareUrl;
    @SerializedName("shelved_url")
    public String shelvedUrl;
    @SerializedName("medium_url")
    public String mediumUrl;

    public String getSquareUrl() {
        return squareUrl == null ? null : toHttp(squareUrl);
    }

    public String getShelvedUrl() {
        return shelvedUrl == null ? null : toHttp(shelvedUrl);
    }

    public String getMediumUrl() {
        return mediumUrl == null ? null : toHttp(mediumUrl);
    }

    private String toHttp(String url) {
        return url.replaceFirst("https://", "http://");
    }
}
