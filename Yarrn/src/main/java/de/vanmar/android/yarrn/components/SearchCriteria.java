package de.vanmar.android.yarrn.components;

/**
 * Created by Kolja on 15.03.14.
 */
public class SearchCriteria {
    private SearchType type;
    private String name;
    private String value;
    private String description;

    public enum SearchType {
        QUERY, USER, CRAFT
    }

    public enum SearchContext {
        PROJECT, STASH, PATTERN
    }

    public SearchCriteria(SearchType type, String name, String value, String description) {
        this.type = type;
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

    public SearchType getType() {
        return type;
    }

    public static SearchCriteria byQuery(String queryText) {
        return new SearchCriteria(SearchCriteria.SearchType.QUERY, "query", queryText, "\"" + queryText + "\"");
    }

    public static SearchCriteria byUser(String user, SearchContext context, String description) {
        if (context == SearchContext.PROJECT) {
            return new SearchCriteria(SearchType.USER, "by", user, description);
        } else if (context == SearchContext.PATTERN) {
            return new SearchCriteria(SearchType.USER, "designer", user, description);
        } else if (context == SearchContext.STASH) {
            return new SearchCriteria(SearchType.USER, "user", user, description);
        }
        return null;
    }

    public static SearchCriteria byFriends(String description) {
        return new SearchCriteria(SearchType.USER, "friends", "yes", description);
    }

    public static SearchCriteria byCraft(String craft, String description) {
        return new SearchCriteria(SearchType.CRAFT, "craft", craft, description);
    }
}
