package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#QueuedProject_small_result
 */
public class QueuedProjectSmall {
    public int id;
    public String name;
    public String notes;
    @SerializedName("pattern_name")
    public String patternName;
    @SerializedName("position_in_queue")
    public int queuePosition;
    @SerializedName("best_photo")
    public Photo bestPhoto;
}
