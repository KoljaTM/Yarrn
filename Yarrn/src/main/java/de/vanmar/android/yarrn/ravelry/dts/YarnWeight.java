package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#YarnWeight__result
 */
public class YarnWeight {
    @SerializedName("name")
    public String name;
    @SerializedName("ply")
    public String ply;
}
