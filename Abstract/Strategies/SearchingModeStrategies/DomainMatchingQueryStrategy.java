package Abstract.Strategies.SearchingModeStrategies;

import Abstract.Engines.DuckDuckGoWebClient;
import Abstract.Engines.FindxWebClient;
import Abstract.Engines.StartPageClient;
import Abstract.Models.DuckDuckGoRequestData;
import Abstract.Models.FindxRequestData;
import Abstract.Models.RequestData;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Strategies.EngineResultsInterpreters.RegularResultsItemsProcess;
import Services.DIResolver;
import Utils.ResultsUtils;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DomainMatchingQueryStrategy extends QueryStrategy {

    private StartPageClient startPageClient;
    private DuckDuckGoWebClient duckDuckGoWebClient;
    private FindxWebClient findxWebClient;
    RegularResultsItemsProcess regularResultsFactory = new RegularResultsItemsProcess();
    private DIResolver diResolver;

    public DomainMatchingQueryStrategy(DIResolver diResolver) {
        startPageClient = new StartPageClient(diResolver);
        duckDuckGoWebClient = new DuckDuckGoWebClient(diResolver);
        findxWebClient = new FindxWebClient(diResolver);
        this.diResolver = diResolver;
    }

    @Override
    public int query(RequestData requestData, AbstractSpecification<GoogleSearchResultItem> googleItemsSpec, Consumer<Set<RegularSearchResultItem>> searchResult) {
        Logger.tag("SYSTEM").info("Retry with StartPage search");
        Set<RegularSearchResultItem> regularSearchResultItems = new HashSet<>();
        boolean isSearchEmail = diResolver.getDbConnectionService().getSearchEmailProperty();
        Logger.tag("SYSTEM").info("StartPage search");
        for (int i = 0; i <= 3; i++) {
            Element startPageBody = getBodyOfStartPage(requestData);
            List<RegularSearchResultItem> startPageSearchResultItems = regularResultsFactory.translateStartPageBodyToModel(startPageBody);
            ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(startPageSearchResultItems, googleItemsSpec);
            regularSearchResultItems.addAll(filteredResult);
            // default start page return 10 records
            if (startPageSearchResultItems.size() == 0 || filteredResult.size() == 0) {
                break;
            }
        }


        if (isSearchEmail || regularSearchResultItems.isEmpty()) {
            Logger.tag("SYSTEM").info("Not result match, retry with DuckDuckGo search");
            DuckDuckGoRequestData duckDuckGoRequestData = new DuckDuckGoRequestData(requestData);

            Element duckDuckGoBody = getBodyOfDuckDuckGo(duckDuckGoRequestData);
            List<RegularSearchResultItem> duckduckGoSearchResultItems = regularResultsFactory.translateDuckDuckGoBodyToModel(duckDuckGoBody);
            duckDuckGoRequestData.setRecordFound(duckduckGoSearchResultItems.size());
            ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(duckduckGoSearchResultItems, googleItemsSpec);
            regularSearchResultItems.addAll(filteredResult);

        }
        if (isSearchEmail || regularSearchResultItems.isEmpty()) {
            Logger.tag("SYSTEM").info("Not result match, retry with Findx search");
            FindxRequestData findxRequestData = new FindxRequestData(requestData);
            // get 5 pages
            for (int i = 0; i <= 5; i++) {
                Element findxBody = getBodyOfFindx(findxRequestData);
                List<RegularSearchResultItem> findxSearchResultItems = regularResultsFactory.translateFindxBodyToModel(findxBody);
                ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(findxSearchResultItems, googleItemsSpec);
                regularSearchResultItems.addAll(filteredResult);
                if (filteredResult.isEmpty()) {
                    break;
                }
            }
        }
        searchResult.accept(regularSearchResultItems);
        return regularSearchResultItems.size();
    }

    private Element getBodyOfStartPage(RequestData requestData) {
        Element body = null;
        try {
            body = startPageClient.requestToSearchEngine(requestData);
        } catch (IOException e) {
            Logger.error(e);
            Logger.tag("SYSTEM").error(e.getMessage());
        }
        return body;
    }

    private Element getBodyOfDuckDuckGo(DuckDuckGoRequestData duckDuckGoRequestData) {
        Element body = null;
        try {
            body = duckDuckGoWebClient.requestToSearchEngine(duckDuckGoRequestData);
        } catch (IOException e) {
            Logger.error(e);
            Logger.tag("SYSTEM").error(e.getMessage());
        }
        return body;
    }

    private Element getBodyOfFindx(RequestData requestData) {
        Element body = null;
        try {
            body = findxWebClient.requestToSearchEngine(requestData);
        } catch (IOException e) {
            Logger.error(e);
            Logger.tag("SYSTEM").error(e.getMessage());
        }
        return body;
    }
}
