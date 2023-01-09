package Abstract.Models.SearchResultModels;

import java.util.Objects;

public class LinkedinSearchResultItem extends RegularSearchResultItem {
    private String role;

    public LinkedinSearchResultItem(String mainHeader, String link, String description) {
        super(mainHeader, link, description);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLink());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Objects.equals(getLink(), ((LinkedinSearchResultItem) o).getLink());
    }
}
