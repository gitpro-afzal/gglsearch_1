package Abstract.Specifications.Concrete;

import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Specifications.AbstractSpecification;

public class InstagramFollowerSpecification extends AbstractSpecification<SocialSearchResultItem> {


    public InstagramFollowerSpecification() {
    }

    @Override
    public boolean isSatisfiedBy(SocialSearchResultItem googleSearchResultItem) {
        googleSearchResultItem.extractFollowers();
        return !googleSearchResultItem.getLink().contains(".com/p/")
                && !"Follower".equalsIgnoreCase(googleSearchResultItem.getUnit())
                && ("k Followers".equalsIgnoreCase(googleSearchResultItem.getUnit()) ? googleSearchResultItem.getFollowers() >= 1 : googleSearchResultItem.getFollowers() > 0);
    }


}
