package de.vanmar.android.yarrn.ravelry.dts;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * http://www.ravelry.com/api#Yarn__result
 */
public class Yarn {
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("yarn_company_name")
    public String yarnCompany;
    @SerializedName("yarn_weight")
    public YarnWeight yarnWeight;
    @SerializedName("yarn_fibers")
    public List<YarnFiber> yarnFibers = new LinkedList<YarnFiber>();
    @SerializedName("photos")
    public List<Photo> photos = new LinkedList<Photo>();
    @SerializedName("notes_html")
    public String notes_html;
}
