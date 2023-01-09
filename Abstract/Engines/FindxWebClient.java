package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.DIResolver;
import Services.UserAgentsRotatorService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FindxWebClient extends ProxyWebClient {
    private static String MAIN_URL = "https://www.findx.com/feed-search";

    public FindxWebClient(DIResolver diResolver) {
        super(diResolver);
    }

    @Override
    public synchronized Element requestToSearchEngine(RequestData requestData) throws IOException {

        String fullRequest = MAIN_URL + requestData.getNextPageQuery();
        for (int i = 0; i < requestData.attemptsCount; i++) {
            CloseableHttpClient client = HttpClients.createMinimal();

            CloseableHttpResponse response = null;
            try {

                URL findx = new URL(fullRequest);
                URLConnection yc = findx.openConnection();
                yc.setRequestProperty("Cookie", "hibext_instdsigdipv2=1; search_client_id=3bbc60eb96d0984f9552153160d8fbf54b3dc71e4bf62d413709e0f6827c91e3a%3A2%3A%7Bi%3A0%3Bs%3A16%3A%22search_client_id%22%3Bi%3A1%3Bs%3A32%3A%221C628686BE2F64DA07FD889CBF366568%22%3B%7D; personalized_search_enabled=050b3db4d9853c22dacb77833eec6fac171330294692ff414b97310c54ac5131a%3A2%3A%7Bi%3A0%3Bs%3A27%3A%22personalized_search_enabled%22%3Bi%3A1%3Bb%3A0%3B%7D; sw=1299; sh=267; adb=1; _csrf-frontend=1a3b6a50e5f8204c38d5e72703ff3091621a611a8e0d30bb82e2b565e55ba955a%3A2%3A%7Bi%3A0%3Bs%3A14%3A%22_csrf-frontend%22%3Bi%3A1%3Bs%3A32%3A%2252td2HthxFeHsvfl8kDUKOOZHley-74-%22%3B%7D");
                StringBuilder res = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    res.append(inputLine).append(" ");
                }
                in.close();
                Logger.tag("SYSTEM").info("Response OK from: " + fullRequest);
                return Jsoup.parse(res.toString());
            } catch (Exception ex) {
                requestData.setLastRequestError(ex.getMessage());
                Logger.tag("SYSTEM").error("Attempt: " + i + " failed. Next attempt in: " + ((requestData.requestDelay * 2) / 1000) + "s. " +
                        "\nCannot get search engine page source, waiting for next attempt: " + fullRequest + " " +
                        "\nCause: " + ex.getMessage());
            } finally {
                if (response != null) {
                    response.close();
                }
                client.close();
                isThreadSleep(i, requestData);
            }
        }
        throw new IOException("Cannot get source: " + fullRequest);
    }
}
