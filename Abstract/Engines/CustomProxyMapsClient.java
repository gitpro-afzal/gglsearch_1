package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.DIResolver;
import kbaa.gsearch.PlaceCard;
import kbaa.gsearch.Search;
import org.tinylog.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomProxyMapsClient extends BaseEngine {

    public CustomProxyMapsClient() {
    }

    public synchronized List<PlaceCard> requestToMapsEngine(RequestData requestData, DIResolver diResolver) throws IOException {
        List<PlaceCard> placeCards = new ArrayList<>();
            Search search = new Search(requestData.getRequestTerm(), getNewClient(null));
            search.setUserAgent(diResolver.getUserAgentsRotatorService().getUserAgent());
            for (int j = 1; j <= requestData.attemptsCount; j++) {
                boolean isContinueWork = diResolver.getDbConnectionService().getWorkStatus();
                if (!isContinueWork) {
                    return placeCards;
                }
                try {
                    Thread.sleep(requestData.requestDelay);
                    search.perform();
                    Logger.tag("SYSTEM").info("Response OK from: " + requestData.requestURL);
                    int i = 0;
                    do {
                        Logger.tag("SYSTEM").info("Processed " + (i + 1) + " page of maps result list.");
                        placeCards.addAll(search.getResults());
                        //diResolver.getGuiService().setStatusText("Found " + placeCards.size() + " items");
                        i++;
                        j = 0;
                        if (search.hasNextPage()) {
                            try {
                                search.loadNextPage();
                            } catch (Exception e) {
                                Logger.tag("SYSTEM").error("Cannot get "+(i + 1)+" page of places list");
                                i--;
                            }
                        } else {
                            break;
                        }
                        isContinueWork = diResolver.getDbConnectionService().getWorkStatus();
                    } while (30 > i && isContinueWork);
                    return placeCards;
                } catch (Exception e) {
                    Logger.tag("SYSTEM").error("Attempt: " + j + " failed. Next attempt in: " + ((requestData.requestDelay * 2) / 1000) + "s. " +
                            "\nCannot get maps engine page source, waiting for next attempt: " + requestData.requestURL + " " +
                            "\nCause: " + e);
                }
                isThreadSleep(j, requestData);
            }
        throw new IOException("Cannot get maps source: " + requestData.requestURL);
    }
}
