package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.UserAgentsRotatorService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

abstract class BaseEngine {
    private static final String username = "lum-customer-ihouse_d-zone-static2";
    private static final String password = "ssb5tj477097";
    private static final String hostName = "zproxy.lum-superproxy.io";
    private static final int port = 22225;
    private final UserAgentsRotatorService userAgentsRotatorService;
    public String session_id = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));

    BaseEngine() {
        userAgentsRotatorService = new UserAgentsRotatorService();
    }

    CloseableHttpClient getNewClient(String country) {

        String login = username+(country!=null ? "-country-"+country : "")
                +"-session-" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE));

        HttpHost super_proxy = new HttpHost(hostName, port);
        CredentialsProvider cred_provider = new BasicCredentialsProvider();
        cred_provider.setCredentials(new AuthScope(super_proxy),
                new UsernamePasswordCredentials(login, password));

        return HttpClients.custom()
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setProxy(super_proxy)
                .setDefaultCredentialsProvider(cred_provider)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .setConnectTimeout(10 * 1000)
                        .setConnectionRequestTimeout(10 * 1000)
                        .setSocketTimeout(10 * 1000)
                        .build())
                .setUserAgent(userAgentsRotatorService.getUserAgent())

                .build();
    }


    void isThreadSleep(int currentAttempt, RequestData requestData) {
        try {
            if (currentAttempt <= requestData.attemptsCount) {
                Thread.sleep(requestData.requestDelay);
            }
        } catch (InterruptedException e) {
            Logger.tag("SYSTEM").error("Interrupt exception");
        }
    }
}
