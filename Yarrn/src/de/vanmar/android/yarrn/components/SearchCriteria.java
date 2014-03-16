package de.vanmar.android.yarrn.components;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchCriteria {
    private String name;
    private String value;
    private String description;

    public SearchCriteria(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
