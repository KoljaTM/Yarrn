package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * http://www.ravelry.com/api#Stash_full_result
 */
public class Stash {
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("location")
    public String location;
    @SerializedName("photos")
    public List<Photo> photos = new LinkedList<Photo>();
    @SerializedName("notes_html")
    public String notes_html;
}
