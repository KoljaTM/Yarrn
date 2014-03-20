package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#Stash_list_result
 */
public class StashShort {
    public int id;
    public String name;
    @SerializedName("first_photo")
    public Photo firstPhoto;
    @SerializedName("location")
    public String location;
    @SerializedName("yarn")
    public Yarn yarn;
    @SerializedName("user")
    public User user;
}
