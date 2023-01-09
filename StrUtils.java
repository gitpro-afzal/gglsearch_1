package Utils;

import Abstract.Models.InputModels.InputCsvModelItem;
import Services.DBConnectionService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.tinylog.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {
    public final static String webSiteUrlRegex = "(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?";
    public final static String newLineDelimiter = System.getProperty("line.separator");

    public enum ResultType {

        DOMAIN, INSTAGRAM_FOLLOWER, SOCIAL;

        private String site;

        public ResultType site(String site) {
            this.site = site;
            return this;
        }

        public String getSite() {
            return site;
        }
    }

    public static String cleanProtocolSchema(String text) {
        return text.replace("\"", "")
                .replaceAll("http://www\\.|https://www\\.|http://|https://|www.", "");
    }

    public synchronized static String extractWebSiteFromURL(String url) {
        Pattern pattern = Pattern.compile(webSiteUrlRegex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return url;
    }

    public static String createQueryForStartPageSearch(InputCsvModelItem csvItem, String inputPlaceHolder) {
        String result = null;
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return "";
        }
        String queryTerm;
        if (csvItem != null) {
            StrSubstitutor sub = new StrSubstitutor(valuesMap(csvItem));
            queryTerm = sub.replace(inputPlaceHolder);
        } else {
            queryTerm = inputPlaceHolder;
        }

        try {
            result = "query=" +
                    URLEncoder.encode(queryTerm, "UTF-8") +
                    "&lui=english&t=default&abp=1&language=english&cat=web";
        } catch (UnsupportedEncodingException e) {
            Logger.tag("SYSTEM").error(e);
        }
        return result;
    }

    public static String createUrlForMultipleSearch(InputCsvModelItem csvItem, String inputPlaceHolder) {
        String result = null;
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return "";
        }
        String queryTerm;
        if (csvItem != null) {
            StrSubstitutor sub = new StrSubstitutor(valuesMap(csvItem));
            queryTerm = sub.replace(inputPlaceHolder);
        } else {
            queryTerm = inputPlaceHolder;
        }

        try {
            result = "https://www.google.com/search?q=" +
                    URLEncoder.encode(queryTerm, "UTF-8") +
                    "&pws=0&gl=us&gws_rd=cr&num=50";
        } catch (UnsupportedEncodingException e) {
            Logger.tag("SYSTEM").error(e);
        }
        return result;
    }

    public static String createSearchTermForMultipleSearch(InputCsvModelItem csvItem, String inputPlaceHolder) {
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return "";
        }
        String queryTerm;
        if (csvItem != null) {
            StrSubstitutor sub = new StrSubstitutor(valuesMap(csvItem));
            queryTerm = sub.replace(inputPlaceHolder);
        } else {
            queryTerm = inputPlaceHolder;
        }
        return queryTerm;
    }

    public static String replacePlaceholderTermsToData(InputCsvModelItem csvItem, String inputPlaceHolder) {
        String result = "";
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return result;
        }
        Pattern pattern = Pattern.compile("\\{column[A-O,a-o]}");

        Matcher m = pattern.matcher(inputPlaceHolder);
        while (m.find()) {
            String column = m.group(0);
            result += " " + createSearchTermForMultipleSearch(csvItem, column);
        }
        return result.trim();
    }

    public static ArrayList<String> extractSearchHolderColumns(String inputPlaceHolder) {
        ArrayList<String> searchColumns = new ArrayList<>();
        if (isPlaceholderHasSubstituteTerms(inputPlaceHolder)) {
            Pattern pattern = Pattern.compile("\\{column[A-O,a-o]}");

            Matcher m = pattern.matcher(inputPlaceHolder);
            while (m.find()) {
                searchColumns.add(m.group(0));
            }
        }
        return searchColumns;
    }

    public static String createUrlForSingleSearch(String inputPlaceHolder) {
        String result = null;
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return "";
        }

        try {
            result = "https://www.google.com/search?q=" +
                    URLEncoder.encode(inputPlaceHolder, "UTF-8") +
                    "&pws=0&gl=us&gws_rd=cr&num=50";
        } catch (UnsupportedEncodingException e) {
            Logger.tag("SYSTEM").error(e);
        }
        return result;
    }

    public static String encodeStringToUTF8(String inputString) {
        String result = null;
        try {
            result = URLEncoder.encode(inputString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.tag("SYSTEM").error(e);
        }
        return result;
    }

    public static String createUrlForGoogleMaps(String inputPlaceHolder) {
        String result = null;
        if (StringUtils.isEmpty(inputPlaceHolder)) {
            return "";
        }

        String pattern = "(?<=&q=)(.*)(?=&)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputPlaceHolder);
        if (m.find()) {
            try {
                result = "https://maps.google.com/maps?q=" +
                        URLEncoder.encode(m.group(0), "UTF-8") +
                        "&pws=0&gl=us&gws_rd=cr&num=150";
            } catch (UnsupportedEncodingException e) {
                Logger.tag("SYSTEM").error(e);
            }
        }
        return result;
    }

    public static String normalizeGoogleLink(String link) {
        if (link.startsWith("http://") || link.startsWith("https://")) {
            return link;
        } else {
            return "http://www.google.com" + link;
        }
    }

    public static String getCityFromAddress(String value) {
        if (value.contains(",")) {
            return value.split(",")[0];
        }
        return value;
    }

    public static String getCountryFromAddress(String value) {
        if (value.contains(",")) {
            return value.split(",")[1];
        }
        return value;
    }

    public static boolean isPlaceholderHasSubstituteTerms(String placeholder) {
        String pattern = "\\{column[A-O,a-o]}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(placeholder);
        return m.find();
    }

    public static boolean isPlaceholderHasSubstituteTerms(String placeholder, String substituteTerm) {
        Pattern r = Pattern.compile(substituteTerm);
        Matcher m = r.matcher(placeholder);
        return m.find();
    }

    public static Map<String, String> valuesMap(InputCsvModelItem csvItem) {
        Map<String, String> valuesMap = new HashMap<String, String>();
        valuesMap.put("columna", replaceCharacters(csvItem.getColumnA()));
        valuesMap.put("columnb", replaceCharacters(csvItem.getColumnB()));
        valuesMap.put("columnc", replaceCharacters(csvItem.getColumnC()));
        valuesMap.put("columnd", replaceCharacters(csvItem.getColumnD()));
        valuesMap.put("columne", replaceCharacters(csvItem.getColumnE()));
        valuesMap.put("columnf", replaceCharacters(csvItem.getColumnF()));
        valuesMap.put("columng", replaceCharacters(csvItem.getColumnG()));
        valuesMap.put("columnh", replaceCharacters(csvItem.getColumnH()));
        valuesMap.put("columni", replaceCharacters(csvItem.getColumnI()));
        valuesMap.put("columnj", replaceCharacters(csvItem.getColumnJ()));
        valuesMap.put("columnk", replaceCharacters(csvItem.getColumnK()));
        valuesMap.put("columnl", replaceCharacters(csvItem.getColumnL()));
        valuesMap.put("columnm", replaceCharacters(csvItem.getColumnM()));
        valuesMap.put("columnn", replaceCharacters(csvItem.getColumnN()));
        valuesMap.put("columno", replaceCharacters(csvItem.getColumnO()));
        return valuesMap;
    }

    private static String replaceCharacters(String value) {
        return value;
//        if (StringUtils.isEmpty(value)) {
//            return value;
//        } else {
//            return value.replaceAll("[^a-zA-Z0-9\\s]", "");
//        }
    }

    public static String getUnmatchedPartOfString(String source) {
        return source.replaceAll("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/?\\n]+)", "");
    }

    public static String extractDomainName(String URL) {
        String result = "";

        Pattern pattern = Pattern.compile("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/?\\n]+)");
        Matcher matcher = pattern.matcher(URL);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }

    public static String cutStringFromEnd(String string, int length) {
        String result = "";
        if (string.length() > length) {
            result = ".." + string.substring(string.length() - length);
        }
        return result;
    }

    private static LevenshteinDistance levenshteinDistance = new LevenshteinDistance();


    public static boolean isMatchedByDistance(List<String> seachedTexts, String domain) {
        double matchLimitScore = 0.8;
        Set<String> matchStr = prepareSearchTexts(seachedTexts);

        String domainName = removeDomainSchema(domain);
        for (String text : matchStr) {
            if (StringUtils.isNotBlank(text)
                    && (domainName.toLowerCase().contains(text.replaceAll(" ", "").toLowerCase())
                    || getLevensteinRatio(text.trim().toLowerCase(), domainName.toLowerCase()) >= matchLimitScore)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> prepareSearchTexts(List<String> seachedTexts) {
        Set<String> matchStr = new HashSet<>(seachedTexts);
        for (int i = 0; i < seachedTexts.size(); i++) {
            String text = seachedTexts.get(i);
            for (int j = 0; j < seachedTexts.size(); j++) {
                if (i != j) {
                    text += " " + seachedTexts.get(j);
                }
            }
            matchStr.add(text.trim());
        }
        matchStr.removeIf(x -> x.length() <= 1);
        return matchStr;
    }

    public static double getMaxScore(List<String> seachedTexts, String domain) {
        TreeSet<Double> orderScore = new TreeSet<>();
        Set<String> matchStr = prepareSearchTexts(seachedTexts);
        String domainName = removeDomainSchema(domain);
        matchStr.forEach(x -> {
            if (StringUtils.isNotBlank(x)) {
                orderScore.add(getLevensteinRatio(x.trim().toLowerCase(), domainName));
            }
        });
        if (orderScore.size() > 0) {

            return orderScore.last();
        }
        return 0;
    }

    public static final String removeDomainSchema(String domain) {
        String domainName = domain.replaceFirst("www.", "");
        if (domainName.lastIndexOf(".") > 0) {
            return domainName.substring(0, domainName.lastIndexOf("."));

        }
        Logger.tag("SYSTEM").warn("Wrong domain: " + domain);
        return domainName;
    }

    public static double getLevensteinRatio(String text, String domain) {
        return 1 - ((double) levenshteinDistance.apply(text, domain)) / Math.max(text.length(), domain.length());
    }

    public static ResultType getResultType(DBConnectionService dbConnectionService) {
        if (dbConnectionService.getExportMatchingDomain()) {
            return ResultType.DOMAIN;
        }
        Pattern pattern = Pattern.compile("(.*)(site:[\\w.]+)(.*)");
        String placeHolder = dbConnectionService.getSearchPlaceholder().replaceAll("http://www\\.|https://www\\.|http://|https://", "");
        Matcher matcher = pattern.matcher(placeHolder);
        if (matcher.matches()) {
            String site = matcher.group(2).replace("site:", "");
            if (site.contains("instagram.com")) {
                return ResultType.INSTAGRAM_FOLLOWER.site("Instagram");

            } else {
                return ResultType.SOCIAL.site(site);
            }
        }
        throw new IllegalArgumentException("site is not specified in search command");

    }
}
