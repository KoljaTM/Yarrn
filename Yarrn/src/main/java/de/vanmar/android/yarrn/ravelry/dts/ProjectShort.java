package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#Project_small_result
 */
public class ProjectShort {
    public int id;
    public String name;
    @SerializedName("pattern_name")
    public String patternName;
    public int progress;
    @SerializedName("first_photo")
    public Photo firstPhoto;
    @SerializedName("user")
    public User user;
}
