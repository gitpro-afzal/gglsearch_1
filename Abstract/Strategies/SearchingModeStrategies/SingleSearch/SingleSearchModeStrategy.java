package Abstract.Strategies.SearchingModeStrategies.SingleSearch;

import Abstract.Engines.*;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.RequestData;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchSettings;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Specifications.Concrete.*;
import Abstract.Strategies.SearchModeStrategyBase;
import Abstract.Strategies.SearchingModeStrategies.ResultModeQueryFactory;
import Abstract.Strategies.SearchingModeStrategies.SearchResultsConvertStrategy;
import Services.DIResolver;
import Services.GuiService;
import Services.OutputDataService;
import Utils.StrUtils;
import kbaa.gsearch.PlaceCard;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Utils.StrUtils.ResultType;

public class SingleSearchModeStrategy extends SearchModeStrategyBase {

    ResultType resultType;

    public SingleSearchModeStrategy(DIResolver diResolver) {
        super(diResolver);
        this.resultType = StrUtils.getResultType(diResolver.getDbConnectionService());
    }


    @Override
    public void processData() {
        StartPageContext.settingPages();
        TorProxyContext.initSockContext(diResolver.getDbConnectionService().getProxyHostProperty(),
                diResolver.getDbConnectionService().getProxyPortProperty());
        InstagramClient.initSockContext(diResolver.getDbConnectionService().getProxyHostProperty(),
                diResolver.getDbConnectionService().getProxyPortProperty());
        OutputDataService outputDataService = diResolver.getOutputDataService();
        GuiService guiService = diResolver.getGuiService();

        if (StringUtils.isEmpty(guiService.getSearchPlaceholderText())) {
            diResolver.getGuiService().changeApplicationStateToWork(false);
            Logger.tag("SYSTEM").info("Placeholder term not specified");
            diResolver.getGuiService().setStatusText("Placeholder term not specified");
            return;
        }
        if (!diResolver.getDbConnectionService().getGoogleSearchEngine() && !diResolver.getDbConnectionService().getGoogleMapsEngine()) {
            diResolver.getGuiService().changeApplicationStateToWork(false);
            Logger.tag("SYSTEM").info("No search method selected");
            diResolver.getGuiService().setStatusText("No search method selected");
            return;
        }

        diResolver.getGuiService().setStatusText("Processing");
        String URL = StrUtils.createUrlForSingleSearch(guiService.getSearchPlaceholderText());
        RequestData requestData = new RequestData(URL, 3, 3000);
        requestData.setRequestTerm(StrUtils.encodeStringToUTF8(guiService.getSearchPlaceholderText()));
        requestData.requestStartPageBody = StrUtils.createQueryForStartPageSearch(null, guiService.getSearchPlaceholderText());
        AbstractSpecification<GoogleSearchResultItem> settingSpec = getSettingsSpecification(diResolver);
        SearchResultsConvertStrategy regularConvertStrategy;
        if (resultType == StrUtils.ResultType.DOMAIN) {

            regularConvertStrategy = new ConvertSearchResultsDataStrategy(diResolver);
        } else if (resultType == ResultType.INSTAGRAM_FOLLOWER) {
            regularConvertStrategy = new InstagramFollowerConvertSingleSearchResultsStrategy(diResolver);
        } else {
            regularConvertStrategy = new LinkedinConvertSingleSearchResultsStrategy(diResolver);
        }
        if (diResolver.getDbConnectionService().getGoogleSearchEngine()) {

            ResultModeQueryFactory.getQueryMode(diResolver).query(requestData, settingSpec, searchResult -> {

                List<IOutputModel> regularItems = regularConvertStrategy.convertResultDataToOutputModels(new ArrayList(searchResult));
                outputDataService.saveResultCsvItems(regularItems);
            });

        } else {
            List<PlaceCard> mapsItems = getPlacesOfMapsResults(requestData);
            List<IOutputModel> scrapedMapsItems = regularConvertStrategy.convertGoogleMapsResultDataToOutputModels(mapsItems);
            outputDataService.saveResultCsvItems(scrapedMapsItems);
        }


        diResolver.getGuiService().changeApplicationStateToWork(false);
        Logger.tag("SYSTEM").info("Finished");
        diResolver.getGuiService().setStatusText("Finished");
    }

    @Override
    public void stopProcessing() {

    }

    @Override
    public void updateStatusText() {

    }

    private List<PlaceCard> getPlacesOfMapsResults(RequestData requestData) {
        List<PlaceCard> mapsItems = null;
        try {
            CustomProxyMapsClient customProxyMapsClient = new CustomProxyMapsClient();
            mapsItems = customProxyMapsClient.requestToMapsEngine(requestData, diResolver);
        } catch (IOException e) {
            Logger.error(e);
            Logger.tag("SYSTEM").error(e.getMessage());
        }
        return mapsItems;
    }

    private AbstractSpecification<GoogleSearchResultItem> getSettingsSpecification(DIResolver diResolver) {

        AbstractSpecification domainExceptionsSpecification = null;
        if (diResolver.getDbConnectionService().getExportMatchingDomain()) {
            SearchSettings searchSettings = diResolver.getDbConnectionService().getSearchSettings();
            ArrayList<String> specificWordInDomain = searchSettings.KeywordsForLookingInDomainURLs;
            if (specificWordInDomain.isEmpty()) {
                Arrays.stream(diResolver.getGuiService().getSearchPlaceholderText().trim().split("\\s+")).forEach(specificWordInDomain::add);
            }


            domainExceptionsSpecification = new DomainExceptionsSpecification(searchSettings.ExceptionsForFoundDomains, null)
                    .and(new TopLevelDomainExceptionsSpecification(searchSettings.ExceptionsForTopLevelDomains, null))
                    .and(new URLExceptionsSpecification(searchSettings.ExceptionsForWordsInDomainURLs, null));

            if (diResolver.getDbConnectionService().getSearchEmailProperty()) {
                domainExceptionsSpecification.and(new EmailSearchSpecification(specificWordInDomain, diResolver.getDbConnectionService().getSearchSettings().AcceptedGuessedDomainEmails, null));
            } else {
                domainExceptionsSpecification.and(new URLSpecificWordsSearchSpecification(specificWordInDomain, null));

            }
        } else if (resultType == ResultType.INSTAGRAM_FOLLOWER) {
            domainExceptionsSpecification = new InstagramFollowerSpecification();
        } else {
            domainExceptionsSpecification = new LinkedinSpecification(diResolver);
        }
        return domainExceptionsSpecification;
    }

}
