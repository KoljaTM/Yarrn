package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * http://www.ravelry.com/api#Project_full_result
 */
public class Project {
    public int id;
    public String name;
    @SerializedName("pattern_name")
    public String patternName;
    public int progress;
    @SerializedName("photos")
    public List<Photo> photos = new LinkedList<Photo>();
    public String status;
}
