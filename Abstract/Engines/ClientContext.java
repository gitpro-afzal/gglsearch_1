package Abstract.Engines;

import Services.UserAgentsRotatorService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Random;

public class ClientContext {
    private static final String username = "lum-customer-ihouse_d-zone-static2";
    private static final String password = "ssb5tj477097";
    private static final String hostName = "zproxy.lum-superproxy.io";
    private static int lastHttpStatus;
    private static String sessionId;

    private static final int port = 22225;
    private static final UserAgentsRotatorService userAgentsRotatorService = new UserAgentsRotatorService();

    public synchronized static String getSessionId() {
        if (lastHttpStatus != 429 && !StringUtils.isBlank(sessionId)) {
            return sessionId;
        }
        sessionId = "-session-" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
        Logger.tag("SYSTEM").info("Renew session: " + sessionId);
        return sessionId;
    }

    private static String getLoginId(String country) {
        return username + (country != null ? "-country-" + country : "")
                + getSessionId();
    }

    public static CloseableHttpResponse execute(HttpUriRequest request, String country) throws IOException {


        HttpHost super_proxy = new HttpHost(hostName, port);
        CredentialsProvider cred_provider = new BasicCredentialsProvider();
        cred_provider.setCredentials(new AuthScope(super_proxy),
                new UsernamePasswordCredentials(getLoginId(country), password));

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(new BasicHttpClientConnectionManager())
//                .setProxy(super_proxy)
                .setDefaultCredentialsProvider(cred_provider)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .setConnectTimeout(10 * 1000)
                        .setConnectionRequestTimeout(10 * 1000)
                        .setSocketTimeout(10 * 1000)
                        .build())
                .setUserAgent(userAgentsRotatorService.getUserAgent())

                .build();


        CloseableHttpResponse response = httpClient.execute(request);
        lastHttpStatus = response.getStatusLine().getStatusCode();
        return response;

    }
}
