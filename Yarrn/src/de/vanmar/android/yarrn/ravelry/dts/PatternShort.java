package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#Pattern_list_result
 */
public class PatternShort {
    public int id;
    public String name;
    @SerializedName("first_photo")
    public Photo firstPhoto;
    @SerializedName("designer")
    public PatternAuthor patternAuthor;
}
