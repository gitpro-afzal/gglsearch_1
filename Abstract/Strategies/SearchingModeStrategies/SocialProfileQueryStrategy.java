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
import Abstract.Strategies.EngineResultsInterpreters.InstagramRegularResultsItemsProcess;
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

public class SocialProfileQueryStrategy extends QueryStrategy {

    private static int TOTAL_RECORDS = 100000;
    private StartPageClient startPageClient;
    private FindxWebClient findxWebClient;
    private DuckDuckGoWebClient duckDuckGoWebClient;

    private RegularResultsItemsProcess regularResultsFactory = new InstagramRegularResultsItemsProcess();

    private DIResolver diResolver;

    public SocialProfileQueryStrategy(DIResolver diResolver) {
        findxWebClient = new FindxWebClient(diResolver);
        startPageClient = new StartPageClient(diResolver);
        duckDuckGoWebClient = new DuckDuckGoWebClient(diResolver);
        this.diResolver = diResolver;
    }

    @Override
    public int query(RequestData requestData, AbstractSpecification<GoogleSearchResultItem> settingSpec, Consumer<Set<RegularSearchResultItem>> searchResult) {
        Logger.tag("SYSTEM").info(" Findx search");
        FindxRequestData findxRequestData = new FindxRequestData(requestData);
        int count = 0;
        HashSet<RegularSearchResultItem> resultItems = new HashSet<>();
        int notFoundCount = 0;
        for (; ; ) {
            if (!diResolver.getDbConnectionService().getWorkStatus()) {
                break;
            }
            Element findxBody = getBodyOfFindx(findxRequestData);
            List<RegularSearchResultItem> findxSearchResultItems = regularResultsFactory.translateFindxBodyToModel(findxBody);
            ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(findxSearchResultItems, settingSpec);
            // filter duplicated record
            filteredResult.removeIf(x -> !resultItems.add(x));
            count += filteredResult.size();
            if (filteredResult.isEmpty()) {
                notFoundCount++;
            }
            if (findxSearchResultItems.isEmpty() || notFoundCount >= 5 || count >= TOTAL_RECORDS) {
                break;
            }
            searchResult.accept(new HashSet<>(filteredResult));

            String status = String.format("Found [%d], Total found [%d], continue...", filteredResult.size(), count);
            diResolver.getGuiService().setStatusText(String.format("Total found [%d], continue...", count));
            Logger.tag("SYSTEM").info(status);
        }
        if (count >= TOTAL_RECORDS) {
            return count;
        }
        Logger.tag("SYSTEM").info("Continue searching data on Startpage.com");
        notFoundCount = 0;
        for (; ; ) {
            if (!diResolver.getDbConnectionService().getWorkStatus()) {
                break;
            }
            Element startPageBody = getBodyOfStartPage(requestData);
            List<RegularSearchResultItem> startPageItems = regularResultsFactory.translateStartPageBodyToModel(startPageBody);
            ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(startPageItems, settingSpec);
            // filter duplicated record
            filteredResult.removeIf(x -> !resultItems.add(x));
            count += filteredResult.size();
            if (filteredResult.isEmpty()) {
                notFoundCount++;
            }
            if (startPageItems.isEmpty() || notFoundCount >= 3 || count >= 100000) {
                break;
            }
            searchResult.accept(new HashSet<>(filteredResult));

            String status = String.format("Found [%d], Total found [%d], continue...", filteredResult.size(), count);
            diResolver.getGuiService().setStatusText(String.format("Total found [%d], continue...", count));
            Logger.tag("SYSTEM").info(status);
        }
        if (count >= TOTAL_RECORDS) {
            return count;
        }
        Logger.tag("SYSTEM").info("Continue searching data on duckduckgo.com");
        notFoundCount = 0;
        for (; ; ) {
            if (!diResolver.getDbConnectionService().getWorkStatus()) {
                break;
            }
            DuckDuckGoRequestData duckDuckGoRequestData = new DuckDuckGoRequestData(requestData);
            Element duckDuckGoBody = getBodyOfDuckDuckGo(duckDuckGoRequestData);
            List<RegularSearchResultItem> searchResultItems= regularResultsFactory.translateDuckDuckGoBodyToModel(duckDuckGoBody);
            ArrayList<RegularSearchResultItem> filteredResult = ResultsUtils.filterResults(searchResultItems, settingSpec);
            // filter duplicated record
            filteredResult.removeIf(x -> !resultItems.add(x));
            count += filteredResult.size();
            if (filteredResult.isEmpty()) {
                notFoundCount++;
            }
            if (searchResultItems.isEmpty() || notFoundCount >= 3 || count >= 100000) {
                break;
            }
            searchResult.accept(new HashSet<>(filteredResult));

            String status = String.format("Found [%d], Total found [%d], continue...", filteredResult.size(), count);
            diResolver.getGuiService().setStatusText(String.format("Total found [%d], continue...", count));
            Logger.tag("SYSTEM").info(status);
        }
        return count;
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
}
