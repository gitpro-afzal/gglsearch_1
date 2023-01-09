package Abstract.Engines;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class InstagramClient {
    private static HttpClientContext context;

    static InstagramClient instagramClient = new InstagramClient();
    static ObjectMapper objectMapper = new ObjectMapper();

    public static void initSockContext(String host, String port) {
        InetSocketAddress socksaddr = new InetSocketAddress(host, Integer.valueOf(port));
        context = HttpClientContext.create();
        context.setAttribute("socks.address", socksaddr);
    }

    public static String getBiography(String url) {
        try {
            Element page = instagramClient.getPageSource(url);
            if (page != null) {
                Element data = page.select("script[type=application/ld+json]").first();
                if (data == null) {
                    return "";
                }
                String description = data.childNode(0).toString();
                Map bioMap = objectMapper.readValue(description, Map.class);
                return (String) bioMap.get("description");
            }

        } catch (Exception ex) {
            Logger.tag("SYSTEM").error(ex, "Could not get instagram bios: " + ex.getMessage());
        }
        return "";
    }

    private CloseableHttpClient getHttpClient() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());

        return HttpClients.custom()
                .disableCookieManagement()
                .disableDefaultUserAgent()
//                .setConnectionManager(cm)
                .build();
    }

    protected boolean isValidResponse(CloseableHttpResponse response) throws IOException {
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return true;
        } else {
            throw new IOException(response != null ? response.getStatusLine().toString() : null);
        }
    }

    public Element getPageSource(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        for (int i = 0; i < 3; i++) {


            CloseableHttpClient closeableHttpClient = null;
            CloseableHttpResponse response = null;
            try {
                closeableHttpClient = getHttpClient();
                response = closeableHttpClient.execute(get);
                if (isValidResponse(response)) {
                    Logger.tag("SYSTEM").info("Response OK from: " + url);
                    String pageSource = EntityUtils.toString(response.getEntity());
                    return Jsoup.parse(pageSource);
                }
            } catch (Exception ex) {
                Logger.tag("SYSTEM").info(ex, "Could not get page source: " + ex.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    // do nothing
                }
            } finally {
                if (response != null) {
                    response.close();
                }
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            }

        }
        return null;
    }
}
