package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
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
    @SerializedName("status_name")
    public String status;
    @SerializedName("started")
    public Date started;
    @SerializedName("started_day_set")
    public boolean startedDaySet;
    @SerializedName("completed")
    public Date completed;
    @SerializedName("completed_day_set")
    public boolean completedDaySet;
}
