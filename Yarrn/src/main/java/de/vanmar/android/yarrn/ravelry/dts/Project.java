package de.vanmar.android.yarrn.ravelry.dts;

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
    @SerializedName("pattern_id")
    public Integer patternId;
    public int progress;
    @SerializedName("photos")
    public List<Photo> photos = new LinkedList<Photo>();
    @SerializedName("project_status_id")
    public int statusId;
    @SerializedName("status_name")
    public String status;
    @SerializedName("notes")
    public String notes;
    @SerializedName("started")
    public Date started;
    @SerializedName("started_day_set")
    public boolean startedDaySet;
    @SerializedName("completed")
    public Date completed;
    @SerializedName("completed_day_set")
    public boolean completedDaySet;
    @SerializedName("rating")
    public int rating;
    @SerializedName("user")
    public User user;
}
