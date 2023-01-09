package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.DIResolver;
import Services.UserAgentsRotatorService;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class DuckDuckGoWebClient extends ProxyWebClient {
    private static String DOCK_DUCK_GO_HTML = "https://duckduckgo.com/html";
    private static UserAgentsRotatorService userAgentsRotatorService = new UserAgentsRotatorService();
    SSLContext sslContext;

    public DuckDuckGoWebClient(DIResolver diResolver) {
        super(diResolver);
        try {
            sslContext =
                    new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized Element requestToSearchEngine(RequestData requestData) throws IOException {

        String fullRequest = DOCK_DUCK_GO_HTML + "?" + requestData.getNextPageQuery();
        HttpGet httpGet = new HttpGet(fullRequest);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:68.0) Gecko/20100101 Firefox/68.0");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader("Accept-Language", "en-US,en;q=0.5");
        httpGet.setHeader("Cookie", "hibext_instdsigdipv2=1; ax=v193-5; c=-1; 1=-1; v=-1");
        for (int i = 0; i < requestData.attemptsCount; i++) {
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(SSLConnectionSocketFactory.getDefaultHostnameVerifier())
                    .setConnectionManager(new BasicHttpClientConnectionManager())
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD)
                            .setConnectTimeout(5 * 1000)
                            .setConnectionRequestTimeout(5 * 1000)
                            .setSocketTimeout(5 * 1000)
                            .build())
                    .build();
            CloseableHttpResponse response = null;
            try {
                response = client.execute(httpGet);
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
