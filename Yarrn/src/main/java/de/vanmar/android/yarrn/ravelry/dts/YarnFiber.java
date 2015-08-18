package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * http://www.ravelry.com/api#YarnFiber__result
 */
public class YarnFiber {
    public int id;
    @SerializedName("percentage")
    public int percentage;
    @SerializedName("fiber_type")
    public FiberType fiberType;

}
