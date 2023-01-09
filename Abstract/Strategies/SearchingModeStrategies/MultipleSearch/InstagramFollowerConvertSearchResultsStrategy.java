package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Engines.InstagramClient;
import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputAdditionalColumnsDataDecorator;
import Abstract.Models.OutputModels.OutputInstaFollowerCSVItem;
import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DIResolver;
import kbaa.gsearch.PlaceCard;

import java.util.ArrayList;
import java.util.List;

public class InstagramFollowerConvertSearchResultsStrategy extends SearchResultsConvertStrategy<SocialSearchResultItem, IOutputModel> {
    public InstagramFollowerConvertSearchResultsStrategy(DIResolver diResolver, InputCsvModelItem inputCsvModelItem) {
        super(inputCsvModelItem, diResolver);
    }

    @Override
    public List<IOutputModel> convertResultDataToOutputModels(List<SocialSearchResultItem> searchItems) {
        final ArrayList<IOutputModel> outputItems = new ArrayList<>();
        searchItems.forEach(searchItem -> {
            OutputInstaFollowerCSVItem instagramCsvItem = new OutputInstaFollowerCSVItem(searchItem.getLink(), searchItem.getFollowers() + searchItem.getUnit(), searchItem.getMainHeader(), "", searchItem.getDescription());
            instagramCsvItem.setInstagramBios(InstagramClient.getBiography(searchItem.getLink()));
            OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                    new OutputAdditionalColumnsDataDecorator(instagramCsvItem, inputCsvModelItem, diResolver.getGuiService().getSearchPlaceholderText());

            outputItems.add(outputModelGeoDataDecorator);
        });
        return outputItems;
    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        return new ArrayList<>();
    }
}
