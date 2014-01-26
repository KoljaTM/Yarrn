package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * http://www.ravelry.com/api#Bookmark_list_result
 */
public class BookmarkShort {

    @SerializedName("comment")
    public String comment;
    @SerializedName("tag_names")
    public List<String> tags;
    @SerializedName("favorited")
    public ProjectShort project;
}
