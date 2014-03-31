package de.vanmar.android.yarrn.components;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchCriteria {
    private String name;
    private String value;
    private String description;

    public enum SearchContext {
        PROJECT, STASH, PATTERN
    }

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

    public static SearchCriteria byUser(String user, SearchContext context, String description) {
        if (context == SearchContext.PROJECT) {
            return new SearchCriteria("by", user, description);
        } else if (context == SearchContext.PATTERN) {
            return new SearchCriteria("designer", user, description);
        } else if (context == SearchContext.STASH) {
            return new SearchCriteria("user", user, description);
        }
        return null;
    }

    public static SearchCriteria byCraft(String craft, String description) {
        return new SearchCriteria("craft", craft, description);
    }
}
