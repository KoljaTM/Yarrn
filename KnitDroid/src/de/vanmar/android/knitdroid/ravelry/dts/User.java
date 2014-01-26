package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#User__result
 */
public class User {
    @SerializedName("id")
    public int id;
    @SerializedName("username")
    public String username;
}
