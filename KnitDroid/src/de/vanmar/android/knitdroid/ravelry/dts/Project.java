package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public String status;
    @SerializedName("created_at")
    public Date started;
    @SerializedName("completed")
    private String completed;
    @SerializedName("completed_day_set")
    public boolean completedDaySet;

    public Date getCompleted() {
        if (completed == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy/MM/dd").parse(completed);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
