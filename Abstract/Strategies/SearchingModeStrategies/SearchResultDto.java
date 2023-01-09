package Abstract.Strategies.SearchingModeStrategies;

import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;

public class SearchResultDto {
    private GoogleSearchResultItem googleSearchResultItem;
    private IOutputModel decorator;
    public SearchResultDto(GoogleSearchResultItem googleSearchResultItem, IOutputModel decorator) {
        this.googleSearchResultItem = googleSearchResultItem;
        this.decorator = decorator;
    }

    public GoogleSearchResultItem getGoogleSearchResultItem() {
        return googleSearchResultItem;
    }

    public IOutputModel getDecorator() {
        return decorator;
    }
}
