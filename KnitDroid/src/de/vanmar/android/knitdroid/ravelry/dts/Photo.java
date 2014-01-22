package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#Photo__result
 */
public class Photo {
	@SerializedName("square_url")
	public String squareUrl;
    @SerializedName("shelved_url")
    public String shelvedUrl;
    @SerializedName("medium_url")
    public String mediumUrl;
}
