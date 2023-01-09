package Abstract.Specifications.Concrete;

import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Services.DIResolver;

import java.util.List;

public class LinkedinSpecification extends AbstractSpecification<SocialSearchResultItem> {

    private List<String> roles;

    public LinkedinSpecification(DIResolver diResolver) {
        roles = diResolver.getDbConnectionService().getSearchSettings().KeywordsForLookingInSearchResults;
    }

    @Override
    public boolean isSatisfiedBy(SocialSearchResultItem googleSearchResultItem) {
        if (roles.isEmpty()) {
            googleSearchResultItem.setUnit("");
            return true;
        }
        String description = googleSearchResultItem.getDescription().toLowerCase();
        for (String role : roles) {
            if (description.contains(role.toLowerCase())) {
                googleSearchResultItem.setUnit(role);
                return true;
            }
        }
        return false;
    }


}
