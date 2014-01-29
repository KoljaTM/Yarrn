package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * This is a mix of several classes, since all types of favorites are returned under the same field
 * <p/>
 * http://www.ravelry.com/api#Project_small_result
 * http://www.ravelry.com/api#Pattern_list_result
 */
public class Favorite {
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
