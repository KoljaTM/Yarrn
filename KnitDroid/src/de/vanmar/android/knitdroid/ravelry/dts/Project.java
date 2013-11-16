package de.vanmar.android.knitdroid.ravelry.dts;

import com.google.gson.annotations.SerializedName;

/**
 * Data structure to represent projects in Ravelry
 */
public class Project {
	public int id;
	public String name;
	@SerializedName("pattern_name")
	public String patternName;
	public int progress;
	@SerializedName("first_photo")
	public Photo firstPhoto;
}
