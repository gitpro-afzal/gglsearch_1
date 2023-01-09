package Abstract.Strategies.SearchingModeStrategies.SingleSearch;

import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputLinkedinCSVItem;
import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DIResolver;
import kbaa.gsearch.PlaceCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinkedinConvertSingleSearchResultsStrategy extends SearchResultsConvertStrategy<SocialSearchResultItem, IOutputModel> {
    public LinkedinConvertSingleSearchResultsStrategy(DIResolver diResolver) {
        super(null, diResolver);
    }

    @Override
    public List<IOutputModel> convertResultDataToOutputModels(List<SocialSearchResultItem> searchItems) {
        return searchItems.stream()
                .map(searchItem -> new OutputLinkedinCSVItem(searchItem.getLink(), searchItem.getUnit(), searchItem.getMainHeader(), "", searchItem.getDescription()))
                .collect(Collectors.toList());

    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        return new ArrayList<>();
    }
}
