package Abstract.Strategies.SearchingModeStrategies.SingleSearch;

import Abstract.Criteria.OneSearchResultCriteria;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputRegularCSVItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Abstract.Models.SearchResultModels.WebPageObject;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Specifications.Concrete.MetaTagsExceptionsSpecification;
import Abstract.Specifications.Concrete.SpecificWordInPageSpecification;
import Abstract.Strategies.SearchingModeStrategies.SearchResultDto;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Utils.ConstsStrings;
import kbaa.gsearch.PlaceCard;
import org.tinylog.Logger;

import java.util.*;

public class ConvertSearchResultsDataStrategy extends SearchResultsConvertStrategy<RegularSearchResultItem, IOutputModel> {


    ConvertSearchResultsDataStrategy(Services.DIResolver diResolver) {
        super(null, diResolver);
    }

    @Override
    public synchronized List<IOutputModel> convertResultDataToOutputModels(List<RegularSearchResultItem> searchItems) {
        final ArrayList<IOutputModel> outputItems = new ArrayList<>();

        if (searchItems.size() == 0) {
            return outputItems;
        }

        int sizeBeforeExclusion = searchItems.size();
        List<String> csvFileData = diResolver.getInputDataService().getInputUrlsExclusionFileItems();
        for (String url : csvFileData) {
            searchItems.removeIf(next -> next.getLink().contains(url));
        }
        Logger.tag("SYSTEM").info("Items excluded: " + (sizeBeforeExclusion - searchItems.size()));

        if (searchItems.stream().noneMatch(x -> ConstsStrings.CONNECTION_ISSUE.equalsIgnoreCase(x.getLink()))) {
            if (diResolver.getDbConnectionService().getSearchSingleResultProperty()) {
                OneSearchResultCriteria oneSearchResultCriteria = new OneSearchResultCriteria(diResolver.getDbConnectionService().getSearchPlaceholder(), null);
                searchItems = oneSearchResultCriteria.meetCriteria(searchItems);
            }
        }

        Services.GuiService guiService = diResolver.getGuiService();
        Services.DBConnectionService dbConnectionService = diResolver.getDbConnectionService();
        AbstractSpecification<WebPageObject> specification = new MetaTagsExceptionsSpecification(dbConnectionService.getSearchSettings().MetaTagsExceptions, null)
                .and(new SpecificWordInPageSpecification(dbConnectionService.getSearchSettings().KeywordsForLookingInSearchResults, null));

        if (diResolver.getDbConnectionService().getSearchEmailProperty() && !searchItems.isEmpty()) {
            searchItems.stream()
                    .map(GoogleSearchResultItem::getMetadatas)
                    .flatMap(Collection::stream)
                    .forEach(metadata -> {
                        OutputRegularCSVItem outputRegularCsv = new OutputRegularCSVItem("", metadata.getWebsite(), metadata.getHeader(), metadata.getEmail(), "", metadata.getStatus(), "");

                        outputItems.add(outputRegularCsv);
                    });

        } else {

            final int searchedItemsSize = searchItems.size();
            List<SearchResultDto> matchingScores = new ArrayList<>();
            for (int i = 0; i < searchedItemsSize; i++) {
                if (dbConnectionService.getWorkStatus()) {
                    guiService.updateCountItemsStatus(i, searchedItemsSize);
                    RegularSearchResultItem searchResultItem = searchItems.get(i);
                    Optional<OutputRegularCSVItem> outputRegularCSVItem = getOutputRegularCSVItem(diResolver, searchResultItem, specification);
                    outputRegularCSVItem.ifPresent(item -> {
                        if (!ConstsStrings.RESULT_NOT_MATCHED.equalsIgnoreCase(item.getNotFound())) {
                            matchingScores.add(new SearchResultDto(searchResultItem, item));
                        }
                    });

                }
            }
            if (diResolver.getDbConnectionService().getSearchSingleResultProperty()) {
                matchingScores.stream()
                        .max(Comparator.comparing(x -> x.getGoogleSearchResultItem().getScore()))
                        .ifPresent(r -> outputItems.add(r.getDecorator()));
            } else {
                matchingScores.forEach(x -> outputItems.add(x.getDecorator()));
            }
        }
        if (outputItems.isEmpty()) {
            OutputRegularCSVItem notFoundItem = new OutputRegularCSVItem(guiService.getSearchPlaceholderText(), "", guiService.getSearchPlaceholderText(), "", "", ConstsStrings.RESULT_NOT_MATCHED, "");
            outputItems.add(notFoundItem);
        }
        return outputItems;
    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        ArrayList<IOutputModel> outputItems = new ArrayList<>();
        if (searchItems == null || searchItems.size() == 0) {
            return outputItems;
        }

        for (PlaceCard placeCard : searchItems) {
            OutputRegularCSVItem outputRegularCSVItem = new OutputRegularCSVItem(placeCard.getName(), placeCard.getSite(), placeCard.getDescription(), placeCard.getAddress(), placeCard.getPhone(), "", "");
            outputItems.add(outputRegularCSVItem);
        }
        return outputItems;
    }

}
