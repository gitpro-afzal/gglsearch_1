package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Criteria.OneSearchResultCriteria;
import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputAdditionalColumnsDataDecorator;
import Abstract.Models.OutputModels.OutputRegularCSVItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Abstract.Models.SearchResultModels.WebPageObject;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Specifications.Concrete.MetaTagsExceptionsSpecification;
import Abstract.Specifications.Concrete.SpecificWordInPageSpecification;
import Abstract.Strategies.SearchingModeStrategies.SearchResultDto;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DBConnectionService;
import Services.DIResolver;
import Utils.ConstsStrings;
import Utils.StrUtils;
import kbaa.gsearch.PlaceCard;
import org.tinylog.Logger;

import java.util.*;

public class ConvertSearchResultsWithDynamicHeadersStrategy extends SearchResultsConvertStrategy<RegularSearchResultItem, IOutputModel> {

    public ConvertSearchResultsWithDynamicHeadersStrategy(DIResolver diResolver, InputCsvModelItem inputCsvModelItem) {
        super(inputCsvModelItem, diResolver);
    }

    @Override
    public synchronized List<IOutputModel> convertResultDataToOutputModels(List<RegularSearchResultItem> searchItems) {
        final ArrayList<IOutputModel> outputItems = new ArrayList<>();
        String searchHolderText = diResolver.getDbConnectionService().getSearchPlaceholder();
        if (StrUtils.isPlaceholderHasSubstituteTerms(searchHolderText)) {
            searchHolderText = StrUtils.replacePlaceholderTermsToData(inputCsvModelItem, searchHolderText);
        }
        if (searchItems.size() == 0) {
            return createNotFoundResult(searchHolderText);
        }

        int sizeBeforeExclusion = searchItems.size();
        List<String> csvFileData = diResolver.getInputDataService().getInputUrlsExclusionFileItems();
        for (String url : csvFileData) {
            searchItems.removeIf(next -> next.getLink().contains(url));
        }
        Logger.tag("SYSTEM").info("Items excluded: " + (sizeBeforeExclusion - searchItems.size()));


        if (searchItems.stream().noneMatch(x -> ConstsStrings.CONNECTION_ISSUE.equalsIgnoreCase(x.getLink()))) {
            if (diResolver.getDbConnectionService().getSearchSingleResultProperty()) {
                OneSearchResultCriteria oneSearchResultCriteria = new OneSearchResultCriteria(diResolver.getDbConnectionService().getSearchPlaceholder(), inputCsvModelItem);
                searchItems = oneSearchResultCriteria.meetCriteria(searchItems);
            }

        } else {
            RegularSearchResultItem errorResult = searchItems.iterator().next();
            searchItems = Collections.singletonList(new RegularSearchResultItem(searchHolderText, errorResult.getLink(), errorResult.getDescription()));
        }
        DBConnectionService dbConnectionService = diResolver.getDbConnectionService();
        AbstractSpecification<WebPageObject> specification = new MetaTagsExceptionsSpecification(dbConnectionService.getSearchSettings().MetaTagsExceptions, null)
                .and(new SpecificWordInPageSpecification(dbConnectionService.getSearchSettings().KeywordsForLookingInSearchResults, null));

        if (diResolver.getDbConnectionService().getSearchEmailProperty() && !searchItems.isEmpty()) {

            GoogleSearchResultItem googleSearchResultItem = searchItems.get(0);
            searchItems.stream()
                    .map(GoogleSearchResultItem::getMetadatas)
                    .flatMap(Collection::stream)
                    .forEach(metadata -> {
                        OutputRegularCSVItem outputRegularCsv = new OutputRegularCSVItem("", metadata.getWebsite(), metadata.getHeader(), metadata.getEmail(), "", metadata.getStatus(), "");
                        OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                                new OutputAdditionalColumnsDataDecorator(outputRegularCsv, inputCsvModelItem, diResolver.getGuiService().getSearchPlaceholderText());

                        outputItems.add(outputModelGeoDataDecorator);
                    });

        } else {
            List<SearchResultDto> matchingScores = new ArrayList<>();
            for (GoogleSearchResultItem googleSearchResultItem : searchItems) {
                if (dbConnectionService.getWorkStatus()) {
                    Optional<OutputRegularCSVItem> outputRegularCSVItem = getOutputRegularCSVItem(diResolver, googleSearchResultItem, specification);
                    outputRegularCSVItem.ifPresent(outputRegularCsv -> {
                        OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                                new OutputAdditionalColumnsDataDecorator(outputRegularCsv, inputCsvModelItem, diResolver.getGuiService().getSearchPlaceholderText());
                        if (!outputRegularCsv.getNotFound().equalsIgnoreCase(ConstsStrings.RESULT_NOT_MATCHED)) {
                            matchingScores.add(new SearchResultDto(googleSearchResultItem, outputModelGeoDataDecorator));
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

            return createNotFoundResult(searchHolderText);

        }
        return outputItems;
    }

    private List<IOutputModel> createNotFoundResult(String searchHolderText) {
        OutputRegularCSVItem notFoundItem = new OutputRegularCSVItem(searchHolderText, "", searchHolderText, "", "", ConstsStrings.RESULT_NOT_MATCHED, "");
        OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                new OutputAdditionalColumnsDataDecorator(notFoundItem, inputCsvModelItem,
                        diResolver.getGuiService().getSearchPlaceholderText());
        return Collections.singletonList(outputModelGeoDataDecorator);
    }

    @Override
    public List<IOutputModel> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems) {
        ArrayList<IOutputModel> outputItems = new ArrayList<>();
        if (searchItems == null || searchItems.size() == 0) {
            return outputItems;
        }

        for (PlaceCard placeCard : searchItems) {
            OutputRegularCSVItem outputRegularCSVItem = new OutputRegularCSVItem(placeCard.getName(), placeCard.getSite(), placeCard.getDescription(), placeCard.getAddress(), placeCard.getPhone(), "", "");
            OutputAdditionalColumnsDataDecorator outputModelGeoDataDecorator =
                    new OutputAdditionalColumnsDataDecorator(outputRegularCSVItem, inputCsvModelItem,
                            diResolver.getGuiService().getSearchPlaceholderText());
            outputItems.add(outputModelGeoDataDecorator);
        }
        return outputItems;
    }
}
