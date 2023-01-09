package Abstract.Models.SearchResultModels;

public class WebPageObject {

    public WebPageObject(String siteDescription, String siteKeywords, String siteName, String pagePlainText, String URL) {
        this.siteDescription = siteDescription;
        this.siteKeywords = siteKeywords;
        this.siteName = siteName;
        this.pagePlainText = pagePlainText;
        this.URL = URL;
    }

    private String siteDescription;
    private String siteKeywords;
    private String siteName;
    private String pagePlainText;
    private String URL;

    public String getSiteDescription() {
        return siteDescription;
    }

    public String getSiteKeywords() {
        return siteKeywords;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getPagePlainText() {
        return pagePlainText;
    }

    public String getURL() {
        return URL;
    }
}
