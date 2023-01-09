package Abstract.Tasks;

import Abstract.Engines.*;
import Abstract.Models.DuckDuckGoRequestData;
import Abstract.Models.RequestData;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Abstract.Models.SearchSettings;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Specifications.Concrete.*;
import Abstract.Strategies.SearchingModeStrategies.MultipleSearch.ConvertSearchResultsWithDynamicHeadersStrategy;
import Abstract.Strategies.SearchingModeStrategies.MultipleSearch.InstagramFollowerConvertSearchResultsStrategy;
import Abstract.Strategies.SearchingModeStrategies.MultipleSearch.SocialCacheResult;
import Abstract.Strategies.SearchingModeStrategies.MultipleSearch.SocialConvertSearchResultsStrategy;
import Abstract.Strategies.SearchingModeStrategies.ResultModeQueryFactory;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DIResolver;
import Utils.ConstsStrings;
import Utils.StrUtils;
import kbaa.gsearch.PlaceCard;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static Utils.StrUtils.ResultType;

public class Worker implements Runnable {

    private final RequestData requestData;
    private final DIResolver diResolver;
    private final AbstractSpecification googleItemsSpec;
    private final CustomProxyMapsClient customProxyMapsClient;
    private final AtomicInteger googleFailedCount;
    ResultType resultType;

    private AbstractSpecification<GoogleSearchResultItem> getSettingsSpecification(DIResolver diResolver) {
        AbstractSpecification domainExceptionsSpecification = null;
        if (diResolver.getDbConnectionService().getExportMatchingDomain()) {
            SearchSettings searchSettings = diResolver.getDbConnectionService().getSearchSettings();
            ArrayList<String> specificWordInDomain = searchSettings.KeywordsForLookingInDomainURLs;
            if (specificWordInDomain.isEmpty()) {
                specificWordInDomain = StrUtils.extractSearchHolderColumns(diResolver.getGuiService().getSearchPlaceholderText());
            }

            domainExceptionsSpecification = new DomainExceptionsSpecification(searchSettings.ExceptionsForFoundDomains, requestData.inputCsvModelItem)
                    .and(new TopLevelDomainExceptionsSpecification(searchSettings.ExceptionsForTopLevelDomains, requestData.inputCsvModelItem))
                    .and(new URLExceptionsSpecification(searchSettings.ExceptionsForWordsInDomainURLs, requestData.inputCsvModelItem));

            if (diResolver.getDbConnectionService().getSearchEmailProperty()) {
                domainExceptionsSpecification.and(new EmailSearchSpecification(specificWordInDomain, diResolver.getDbConnectionService().getSearchSettings().AcceptedGuessedDomainEmails, requestData.inputCsvModelItem));
            } else {
                domainExceptionsSpecification.and(new URLSpecificWordsSearchSpecification(specificWordInDomain, requestData.inputCsvModelItem));

            }
        } else if (resultType == ResultType.INSTAGRAM_FOLLOWER) {
            domainExceptionsSpecification = new InstagramFollowerSpecification();

        } else {
            domainExceptionsSpecification = new LinkedinSpecification(diResolver);
        }


        return domainExceptionsSpecification;

    }

    public Worker(DIResolver diResolver, RequestData requestData, AtomicInteger googleFailedCount) {
        this.customProxyMapsClient = new CustomProxyMapsClient();
        this.diResolver = diResolver;
        this.requestData = requestData;
        this.resultType = StrUtils.getResultType(diResolver.getDbConnectionService());
        this.googleItemsSpec = getSettingsSpecification(diResolver);
        this.googleFailedCount = googleFailedCount;
    }

    @Override
    public void run() {
        if (diResolver.getDbConnectionService().getWorkStatus()) {
            SearchResultsConvertStrategy regularConvertStrategy;
            if (diResolver.getDbConnectionService().getExportMatchingDomain()) {
                regularConvertStrategy = new ConvertSearchResultsWithDynamicHeadersStrategy(diResolver, requestData.inputCsvModelItem);
            } else if (resultType == ResultType.INSTAGRAM_FOLLOWER) {
                regularConvertStrategy = new InstagramFollowerConvertSearchResultsStrategy(diResolver, requestData.inputCsvModelItem);
            } else {
                regularConvertStrategy = new SocialConvertSearchResultsStrategy(diResolver, requestData.inputCsvModelItem);
            }

            List<PlaceCard> mapsItems;
            if (diResolver.getDbConnectionService().getGoogleSearchEngine()) {
                try {
                    ResultModeQueryFactory.getQueryMode(diResolver).query(requestData, googleItemsSpec, regularSearchResultItems -> {
                        List nonDuplicatedRecords = SocialCacheResult.addAll(regularSearchResultItems);
                        if (!nonDuplicatedRecords.isEmpty() || regularSearchResultItems.isEmpty()) {
                            List regularItems = regularConvertStrategy.convertResultDataToOutputModels(nonDuplicatedRecords);
                            diResolver.getOutputDataService().saveResultCsvItemsByMultipleSearch(regularItems);
                        }
                    });

                } catch (Exception ex) {
                    List<RegularSearchResultItem> filteredRegularSearchResultItems = Collections.singletonList(new RegularSearchResultItem("", ConstsStrings.CONNECTION_ISSUE, requestData.lastRequestError));
                    List regularItems = regularConvertStrategy.convertResultDataToOutputModels(filteredRegularSearchResultItems);
                    diResolver.getOutputDataService().saveResultCsvItemsByMultipleSearch(regularItems);
                }
            } else {
                mapsItems = getPlacesOfMapsResults();
                List scrapedMapsItems = regularConvertStrategy.convertGoogleMapsResultDataToOutputModels(mapsItems);
                diResolver.getOutputDataService().saveResultCsvItemsByMultipleSearch(scrapedMapsItems);


            }


        }
    }

    private List<PlaceCard> getPlacesOfMapsResults() {
        List<PlaceCard> mapsItems = null;
        try {
            mapsItems = customProxyMapsClient.requestToMapsEngine(requestData, diResolver);
        } catch (IOException e) {
            Logger.error(e);
            Logger.tag("SYSTEM").error(e.getMessage());
        }
        return mapsItems;
    }

}
