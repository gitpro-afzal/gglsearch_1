package Abstract.Strategies.EngineResultsInterpreters;

import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Utils.StrUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tinylog.Logger;
import sun.net.util.IPAddressUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RegularResultsItemsProcess {

    public List<RegularSearchResultItem> translateFindxBodyToModel(Element body) {
        ArrayList<RegularSearchResultItem> results = new ArrayList<>();
        if (body == null) {
            return results;
        }
        Elements resultDivs = body.select("div[class=article]");
        for (Element div : resultDivs) {
            String mainHeader = div.select("h3").select("a").text();

            String link =div.select("a[class=result-link]").text();
            if (StringUtils.isEmpty(link)) {
                link = div.select("h3").attr("href");
            }

            if(StringUtils.isBlank(link)) {
                break;
            }
            link = cleanLink(link);
            String description = div.select("p[class=result-description]").text();

            RegularSearchResultItem regularSearchResultItem = createRegularSearchResultItem(mainHeader,link, description);
            if (StringUtils.isNotBlank(regularSearchResultItem.getHostName()) && isNotIpAddress(regularSearchResultItem.getHostName())) {
                results.add(regularSearchResultItem);
            }
        }
        return results.stream().distinct().collect(Collectors.toList());

    }
    public List<RegularSearchResultItem> translateDuckDuckGoBodyToModel(Element body) {
        ArrayList<RegularSearchResultItem> results = new ArrayList<>();
        if (body == null) {
            return results;
        }
        Elements resultDivs = body.select("div[class=result results_links results_links_deep web-result]");
        for (Element div : resultDivs) {
            String mainHeader = div.select("h2[class=result__title]").text();
            if (StringUtils.isEmpty(mainHeader)) {
                mainHeader = div.select("h2").text();
            }
            String link =div.select("div[class=result__extras__url]").select("a[class=result__url]").text();
            if (StringUtils.isEmpty(link)) {
                link = div.select("h3").attr("href");
            }
            if (StringUtils.isEmpty(link)) {
                link = div.select("div.r > a").attr("href");
            }
            if(StringUtils.isBlank(link)) {
                break;
            }
            int paramIdx = link.indexOf("/?");
            if(link.indexOf(paramIdx) > 0) {
                link = link.substring(0, paramIdx);
            }
            link = cleanLink(link);
            String description = div.select("a[class=result__snippet]").text();

            RegularSearchResultItem regularSearchResultItem = createRegularSearchResultItem(mainHeader,link, description);
            if (StringUtils.isNotBlank(regularSearchResultItem.getHostName()) && isNotIpAddress(regularSearchResultItem.getHostName())) {
                results.add(regularSearchResultItem);
            }
        }
        return results.stream().distinct().collect(Collectors.toList());

    }

    protected RegularSearchResultItem createRegularSearchResultItem(String mainHeader, String link, String description) {
        return new RegularSearchResultItem(mainHeader, StrUtils.extractWebSiteFromURL(link), description);
    }

    public List<RegularSearchResultItem> translateStartPageBodyToModel(Element body) {
        ArrayList<RegularSearchResultItem> results = new ArrayList<>();
        if (body == null) {
            return results;
        }
        Element searchedBody = (Element) body.childNode(1);

        Elements resultDivs = searchedBody.select("div[class=w-gl__result]:not(:contains(Images for))");
        for (Element div : resultDivs) {
            String mainHeader = div.select("a[class=w-gl__result-title]").text();
            if (StringUtils.isEmpty(mainHeader)) {
                mainHeader = div.select("h3").text();
            }
            String link = div.select("a[class=w-gl__result-url]").attr("href");
            if (StringUtils.isEmpty(link)) {
                link = div.select("h3").attr("href");
            }
            if (StringUtils.isEmpty(link)) {
                link = div.select("div.r > a").attr("href");
            }
            link = cleanLink(link);
            String description = div.select("p[class=w-gl__description]").text();

            RegularSearchResultItem regularSearchResultItem = createRegularSearchResultItem(mainHeader, link, description);
            if (StringUtils.isNotBlank(regularSearchResultItem.getHostName()) && isNotIpAddress(regularSearchResultItem.getHostName())) {
                results.add(regularSearchResultItem);
            }
        }
        return results.stream().distinct().collect(Collectors.toList());

    }

    protected String cleanLink(String rawLink) {
        String link = rawLink.replaceFirst("/*$", "");
        try {
            return URLDecoder.decode(link, "utf8");
        } catch (UnsupportedEncodingException e) {
            return link;
        }

    }

    public List<RegularSearchResultItem> translateBodyToModels(Element body) {
        ArrayList<RegularSearchResultItem> results = new ArrayList<>();
        if (body == null) {
            return results;
        }
        Elements elItems = body.select("#ires");
        if (elItems.size() == 0) {
            elItems = body.select("#res");
        }
        Element items = null;
        if (elItems != null) {
            items = elItems.first();
        }

        if (items == null) {
            return results;
        }
        Elements resultDivs = items.select("div[class=g]:not(:contains(Images for))");
        Logger.tag("SYSTEM").info("Parsed: " + resultDivs.size() + " links");
        for (Element div : resultDivs) {
            String mainHeader = div.select("h3.r > a").text();
            if (StringUtils.isEmpty(mainHeader)) {
                mainHeader = div.select("h3").text();
            }
            String link = div.select("h3.r > a").attr("href");
            if (StringUtils.isEmpty(link)) {
                link = div.select("h3").attr("href");
            }
            if (StringUtils.isEmpty(link)) {
                link = div.select("div.r > a").attr("href");
            }

            String description = div.select("div.s").select("span.st").text();

            RegularSearchResultItem regularSearchResultItem = createRegularSearchResultItem(mainHeader, StrUtils.extractWebSiteFromURL(link), description);
            if (StringUtils.isNotBlank(regularSearchResultItem.getHostName()) && isNotIpAddress(regularSearchResultItem.getHostName())) {
                results.add(regularSearchResultItem);
            }
        }
        return results.stream().distinct().collect(Collectors.toList());
    }

    private boolean isNotIpAddress(String hostName) {
        try {
            return !IPAddressUtil.isIPv4LiteralAddress(hostName);
        } catch (Exception ex) {
            return true;
        }
    }
}
