package Abstract.Engines;

import Abstract.Models.RequestData;
import Services.DIResolver;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ProxyWebClient extends BaseEngine {

    private DIResolver diResolver;
    private static final String CUSTOMER = "ihouse_d";
    private static final String EMAIL = "ihouse@ihousedesign.com";
    private static final String PASSWORD = "Foxtalbot123!";

    public ProxyWebClient(DIResolver diResolver) {
        this.diResolver = diResolver;
    }

    protected CloseableHttpResponse execute(HttpUriRequest request, String country) throws IOException {
        return ClientContext.execute(request, country);
    }

    public boolean isWebsiteAlive(String website) {
        HttpGet heathCheckRequest = new HttpGet(website);
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .setConnectTimeout(10 * 1000)
                        .setConnectionRequestTimeout(10 * 1000)
                        .setSocketTimeout(10 * 1000)
                        .build())
                .build();
        for (int i = 0; i < 3; i++) {

            try {
                client.execute(heathCheckRequest);
                return true;
            } catch (IOException e) {
                Logger.tag("SYSTEM").error("Cannot ping to website: " + website + ".Attempt [" + (i + 1) + "] Retry in 5 secs");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    // do nothing
                }
            }
        }
        return false;
    }

    public synchronized Element requestToSearchEngine(RequestData requestData) throws IOException {
        for (int i = 1; i <= requestData.attemptsCount; i++) {
            boolean isContinueWork = diResolver.getDbConnectionService().getWorkStatus();
            if (!isContinueWork) {
                return null;
            }
            try {
                Thread.sleep(requestData.requestDelay);
            } catch (InterruptedException e) {
                Logger.tag("SYSTEM").error(e);
            }
            HttpGet request;
            CloseableHttpClient closeableHttpClient = null;
            CloseableHttpResponse response = null;
            try {
                request = new HttpGet(requestData.requestURL);
                // change proxy in if last request has error
                response = execute(request, null);
//                System.out.println(EntityUtils.toString(response.getEntity()));
                if (isValidResponse(response)) {
                    Logger.tag("SYSTEM").info("Response OK from: " + requestData.requestURL);
                    String pageSource = EntityUtils.toString(response.getEntity());
                    return Jsoup.parse(pageSource);
                }
            } catch (Exception ex) {
                requestData.setLastRequestError(ex.getMessage());
                Logger.tag("SYSTEM").error("Attempt: " + i + " failed. Next attempt in: " + ((requestData.requestDelay * 2) / 1000) + "s. " +
                        "\nCannot get search engine page source, waiting for next attempt: " + requestData.requestURL + " " +
                        "\nCause: " + ex.getMessage());
            } finally {
                if (response != null) {
                    response.close();
                }
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
                isThreadSleep(i, requestData);
            }
        }
        throw new IOException("Cannot get source: " + requestData.requestURL);
    }

    private void handleError407() {
        if (isBalanceValid()) {
            whitelistIp();
        } else {
            throw new IllegalStateException("Insufficient Balance! Please deposit your balance on luminati.io for account " + EMAIL);
        }

    }

    private boolean isBalanceValid() {
        HttpClient client = HttpClientBuilder.create().build();
        String url = "https://luminati.io/api/balance?customer=" + CUSTOMER;
        HttpGet httpGet = new HttpGet(url);
        String authorization = "Basic " + Base64.getEncoder().encodeToString((EMAIL + ":" + PASSWORD).getBytes());
        httpGet.addHeader("Authorization", authorization);
        try {
            HttpResponse response = client.execute(httpGet);
            JSONObject rs = (JSONObject) JSONValue.parse(new InputStreamReader(response.getEntity().getContent()));

            return (double) rs.get("balance") > 0;
        } catch (IOException e) {
            Logger.error("Cannot get balance: " + e.getMessage(), e);
            return false;
        }
    }

    private void whitelistIp() {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("https://luminati.io/api/whitelist/add");
        String publicIp = getPublicIp();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email", EMAIL));
        params.add(new BasicNameValuePair("password", PASSWORD));
        params.add(new BasicNameValuePair("customer", CUSTOMER));
        params.add(new BasicNameValuePair("zone", "static2"));
        params.add(new BasicNameValuePair("ip", publicIp));

        try {
            String authorization = "Basic " + Base64.getEncoder().encodeToString((EMAIL + ":" + PASSWORD).getBytes());
            httpPost.addHeader("Authorization", authorization);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            Logger.tag("SYSTEM").info("White list ip[" + publicIp + "] result with status: "
                    + response.getStatusLine().getStatusCode()
                    + " - " + response.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            Logger.tag("SYSTEM").error("Fail to White list ip[" + publicIp + "]" + e.getMessage(), e);
        }

    }

    private String getPublicIp() {
        HttpGet httpGet = new HttpGet("http://lumtest.com/myip");
        try {
            return EntityUtils.toString(HttpClients.createDefault().execute(httpGet).getEntity());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected boolean isValidResponse(CloseableHttpResponse response) throws IOException {
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            throw new IOException(response != null ? response.getStatusLine().toString() : null);
        }
    }
}