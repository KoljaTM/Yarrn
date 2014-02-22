package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#FiberType_result
 */
public class FiberType {
    public int id;
    @SerializedName("name")
    public String name;
}
