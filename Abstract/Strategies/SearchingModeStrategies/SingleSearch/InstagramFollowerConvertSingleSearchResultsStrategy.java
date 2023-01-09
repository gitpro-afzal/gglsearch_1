package Abstract.Strategies.SearchingModeStrategies.SingleSearch;

import Abstract.Engines.InstagramClient;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputInstaFollowerCSVItem;
import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DIResolver;
import kbaa.gsearch.PlaceCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InstagramFollowerConvertSingleSearchResultsStrategy extends SearchResultsConvertStrategy<SocialSearchResultItem, IOutputModel> {
    public InstagramFollowerConvertSingleSearchResultsStrategy(DIResolver diResolver) {
        super(null, diResolver);
    }

    @Override
    public List<IOutputModel> convertResultDataToOutputModels(List<SocialSearchResultItem> searchItems) {
        return searchItems.stream()
                .map(searchItem -> {
                    OutputInstaFollowerCSVItem instagramCsvItem = new OutputInstaFollowerCSVItem(searchItem.getLink(), searchItem.getFollowers() + searchItem.getUnit(), searchItem.getMainHeader(), "", searchItem.getDescription());
                    instagramCsvItem.setInstagramBios(InstagramClient.getBiography(searchItem.getLink()));
                    return instagramCsvItem;

                })
                .collect(Collectors.toList());

    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        return new ArrayList<>();
    }
}
