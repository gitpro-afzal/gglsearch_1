package Abstract.Engines;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.tinylog.Logger;

import java.io.IOException;

public class StartPageContext {

    public static void settingPages() {

        CloseableHttpClient client =  HttpClients.custom()
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .setConnectTimeout(10 * 1000)
                        .setConnectionRequestTimeout(10 * 1000)
                        .setSocketTimeout(10 * 1000)
                        .build())
                .build();
        String requestHost = "https://www.startpage.com/search/settings?lang=en";
        String requestBody = "pref_language_ui=english&pref_language=english&pref_homepage=&pref_homepage_theme=default&pref_enable_stay_control=1&pref_suggestions=1&pref_geo_map=1&pref_wikipedia_ia=1&pref_other_ia=1&pref_wt_unit=celsius&pref_num_of_results=20&pref_disable_open_in_new_window=1&vf_disable_video_family_filter=1&pref_enable_post_method=1&pref_enable_proxy_safety_suggest=1&pref_disable_family_filter=&pref_connect_to_server=default&pref_ssl=1&cat=web";
        HttpPost httpPost = new HttpPost(requestHost);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Cookie", "preferences=lang_homepageEEEs/default/en/N1Nresults_countEEE0N1Nlanguage_uiEEEenglishN1Ndisable_open_in_new_windowEEE0N1Nwt_unitEEEcelsiusN1NlanguageEEEenglishN1NsslEEE1N1Nnum_of_resultsEEE20N1NsuggestionsEEE1N1Ngeo_mapEEE1N1N");
        try {
            httpPost.setEntity(new StringEntity(requestBody));
            client.execute(httpPost);
        } catch (IOException e) {
            Logger.tag("SYSTEM").error("Cannot setting start page" + e.getMessage(), e);
        }
    }
}
