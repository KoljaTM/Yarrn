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
}
