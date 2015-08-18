package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * http://www.ravelry.com/api#QueuedProject_full_result
 */
public class QueuedProject {
    public int id;
    public String name;
    @SerializedName("pattern_name")
    public String patternName;
    @SerializedName("pattern")
    public Pattern pattern;
    @SerializedName("notes")
    public String notes;
    @SerializedName("created_at")
    public Date created;
}
