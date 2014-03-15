package de.vanmar.android.yarrn.components;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchCriteria {
    private String name;
    private String value;

    public SearchCriteria(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
