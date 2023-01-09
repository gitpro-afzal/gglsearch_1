package Abstract.Engines;

import Services.UserAgentsRotatorService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TorProxyContext {

    private static HttpClientContext context;

    private static class SessionInfo {
        private String sessionId;
        private String userAgent;
        private int lastHttpStatus;
        private List<String> sessions = new ArrayList<>();
    }

    public static void initSockContext(String host, String port) {
        InetSocketAddress socksaddr = new InetSocketAddress(host, Integer.valueOf(port));
        context = HttpClientContext.create();
        context.setAttribute("socks.address", socksaddr);
    }

    private static Map<Long, SessionInfo> sessionInfos = new ConcurrentHashMap<>();

    private static CloseableHttpClient getHttpClient() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault()))
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());

        return HttpClients.custom()
                .disableCookieManagement()
                .disableDefaultUserAgent()
                .setConnectionManager(cm).build();
    }

    private static UserAgentsRotatorService userAgentsRotatorService = new UserAgentsRotatorService();

    public static SessionInfo getSessionInfo() {
        long threadId = Thread.currentThread().getId();
        SessionInfo sessionInfo = sessionInfos.getOrDefault(threadId, null);
        if(sessionInfo ==null) {
            sessionInfo = new SessionInfo();
            sessionInfos.putIfAbsent(threadId, sessionInfo);
        }

        if (sessionInfo.lastHttpStatus == 429 || StringUtils.isBlank(sessionInfo.sessionId)) {

            sessionInfo.userAgent = userAgentsRotatorService.getRandomUserAgent();
            sessionInfo.sessionId = "-session-" + Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
            sessionInfo.sessions.clear();
            Logger.tag("SYSTEM").info("Renew session - Thread ["+threadId+"] " + sessionInfo.sessionId);

        }

        return sessionInfo;
    }

    public static CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        SessionInfo sessionInfo = getSessionInfo();

        CloseableHttpClient httpClient = getHttpClient();
        if (sessionInfo.sessions.isEmpty()) {
            Set<String> acceptedCookies = new HashSet<>();

            acceptedCookies.add("1P_JAR");
            acceptedCookies.add("CGIC");
            acceptedCookies.add("NID");
            acceptedCookies.add("ANID");
            acceptedCookies.add("SID");
            acceptedCookies.add("HSID");
            acceptedCookies.add("SIDCC");
            acceptedCookies.add("APISID");
            acceptedCookies.add("SSID");


            HttpGet initGoogleSession = new HttpGet("https://www.google.com");
            initGoogleSession.addHeader("User-Agent", sessionInfo.userAgent);
            CloseableHttpResponse response = httpClient.execute(initGoogleSession, context);
            Header[] cookies = response.getHeaders("Set-Cookie");

            sessionInfo.sessions.add("CGIC=IgMqLyo");
            for (Header cookie : cookies) {

                for (HeaderElement element : cookie.getElements()) {
                    if (acceptedCookies.contains(element.getName().toUpperCase().trim())) {
                        sessionInfo.sessions.add(element.getName() + "=" + element.getValue());
                    }
                }
            }
            sessionInfo.sessions.add("ANID=AHWqTUnhMFPqDn9dxBd_pwP5HR8uMJSU1t3ugHTOAhnontOG_7qz77Pd3rMfCuMh; SID=pQeQsmsFC3N1O-z98B9L5rUagCfG6sBosk_l5wz_oomEi9fUIbDAr8IAQ03fqSYZT55iPw.; HSID=AlxFOnCOM708w64tJ; SSID=AanVCDq0Dr2ryDcc4; APISID=qc60-rVXTVPmYOrT/AlqEQZe-gNwAJ9CYq; SAPISID=P545seuUijqlQZmu/A5_Z_ZINGwjyln6HH; SIDCC=AN0-TYvJTj44vMKuxGX074oJtDcMZIrMqWAqOTJFZ7YwgI8bsV3F8ZsAk5E-NW5cWo7sW0sKfzo; DV=A-ELs9UTgxsWYPXf0FOdhZBXcQlA3RY");


        }

        request.addHeader("Host", "www.google.com");
        request.addHeader("User-Agent", sessionInfo.userAgent);
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Language", "en-US;q=0.5,en;q=0.3");
        request.addHeader("Cookie", sessionInfo.sessions.stream().collect(Collectors.joining(";")));
        CloseableHttpResponse response = httpClient.execute(request, context);
        sessionInfo.lastHttpStatus = response.getStatusLine().getStatusCode();

        return response;

    }

    private static Object lock = new Object();
}
