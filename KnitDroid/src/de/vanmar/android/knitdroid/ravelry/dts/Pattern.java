package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * http://www.ravelry.com/api#Pattern_full_result
 */
public class Pattern {
    public int id;
    public String name;
    @SerializedName("photos")
    public List<Photo> photos = new LinkedList<Photo>();
    @SerializedName("pattern_author")
    public PatternAuthor patternAuthor;
    @SerializedName("notes_html")
    public String notes_html;
    @SerializedName("yarn_weight_description")
    public String yarn_weight_description;
    @SerializedName("yardage_description")
    public String yardage_description;
    @SerializedName("gauge_description")
    public String gauge_description;
    @SerializedName("pattern_needle_sizes")
    public List<Needle> pattern_needle_sizes;
}
