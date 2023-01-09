package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.DIResolver;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;

public class StartPageClient extends ProxyWebClient {

    private static final String START_PAGE_DOMAIN = "https://www.startpage.com/do/search";

    public StartPageClient(DIResolver diResolver) {
        super(diResolver);
    }

    @Override
    public synchronized Element requestToSearchEngine(RequestData requestData) throws IOException {


        String requestBody = requestData.getNextPageQuery();
        HttpPost httpPost = new HttpPost(START_PAGE_DOMAIN);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Cookie","preferences=lang_homepageEEEs/default/en/N1Nenable_post_methodEEE0N1Nresults_countEEE0N1Nlanguage_uiEEEenglishN1Ndisable_open_in_new_windowEEE0N1Nwt_unitEEEcelsiusN1NlanguageEEEenglishN1NsslEEE1N1Nenable_proxy_safety_suggestEEE0N1Nnum_of_resultsEEE20N1Ndisable_video_family_filterEEE1N1NsuggestionsEEE1N1Ngeo_mapEEE1N1N");

        httpPost.setEntity(new StringEntity(requestBody));
        String fullRequest = START_PAGE_DOMAIN + "?" + requestBody;
        for (int i = 0; i < requestData.attemptsCount; i++) {
            CloseableHttpClient client = HttpClients.custom()
                    .setConnectionManager(new BasicHttpClientConnectionManager())
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .setConnectTimeout(10 * 1000)
                            .setConnectionRequestTimeout(10 * 1000)
                            .setSocketTimeout(10 * 1000)
                            .build())
                    .build();
            CloseableHttpResponse response = null;
            try {
                response = client.execute(httpPost);
                if (isValidResponse(response)) {
                    Logger.tag("SYSTEM").info("Response OK from: " + fullRequest);
                    String pageSource = EntityUtils.toString(response.getEntity());
                    return Jsoup.parse(pageSource);
                }
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
