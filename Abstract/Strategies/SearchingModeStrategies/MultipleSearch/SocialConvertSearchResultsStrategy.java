package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

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

public class SocialConvertSearchResultsStrategy extends SearchResultsConvertStrategy<SocialSearchResultItem, IOutputModel> {
    public SocialConvertSearchResultsStrategy(DIResolver diResolver, InputCsvModelItem inputCsvModelItem) {
        super(inputCsvModelItem, diResolver);
    }

    @Override
    public List<IOutputModel> convertResultDataToOutputModels(List<SocialSearchResultItem> searchItems) {
        final ArrayList<IOutputModel> outputItems = new ArrayList<>();
        searchItems.forEach(searchItem -> {
            OutputInstaFollowerCSVItem outputRegularCsv = new OutputInstaFollowerCSVItem(searchItem.getLink(), searchItem.getUnit(), searchItem.getMainHeader(), "", searchItem.getDescription());
            OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                    new OutputAdditionalColumnsDataDecorator(outputRegularCsv, inputCsvModelItem, diResolver.getGuiService().getSearchPlaceholderText());

            outputItems.add(outputModelGeoDataDecorator);
        });
        return outputItems;
    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        return new ArrayList<>();
    }
}
