package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * http://www.ravelry.com/api#Bookmark_list_result
 */
public class BookmarkShort {

    public static final String PROJECT = "project";
    public static final String PATTERN = "pattern";

    @SerializedName("comment")
    public String comment;
    @SerializedName("type")
    public String type;
    @SerializedName("tag_names")
    public List<String> tags;
    @SerializedName("favorited")
    public Favorite favorite;
}
