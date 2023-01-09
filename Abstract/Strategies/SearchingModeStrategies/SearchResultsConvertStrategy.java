package Abstract.Strategies.SearchingModeStrategies;

import Abstract.Engines.ProxyWebClient;
import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.OutputModels.IOutputModel;
import Abstract.Models.OutputModels.OutputRegularCSVItem;
import Abstract.Models.RequestData;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.WebPageObject;
import Abstract.Specifications.AbstractSpecification;
import Services.DIResolver;
import Utils.ConstsStrings;
import Utils.StrUtils;
import kbaa.gsearch.PlaceCard;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public abstract class SearchResultsConvertStrategy<T extends GoogleSearchResultItem, U extends IOutputModel> {
    protected InputCsvModelItem inputCsvModelItem;
    protected final DIResolver diResolver;

    public abstract List<U> convertResultDataToOutputModels(List<T> searchItems);

    public abstract List<U> convertGoogleMapsResultDataToOutputModels(List<PlaceCard> searchItems);

    public SearchResultsConvertStrategy(InputCsvModelItem inputCsvModelItem, DIResolver diResolver) {
        this.inputCsvModelItem = inputCsvModelItem;
        this.diResolver = diResolver;
    }

    private String getCompanyName(WebPageObject webPageObject, GoogleSearchResultItem googleSearchResultItem) {
        if (StringUtils.isEmpty(googleSearchResultItem.getMainHeader())) {
            return webPageObject.getSiteName();
        }
        return googleSearchResultItem.getMainHeader();
    }

    protected Optional<OutputRegularCSVItem> getOutputRegularCSVItem(DIResolver diResolver, GoogleSearchResultItem googleSearchResultItem, AbstractSpecification<WebPageObject> specification) {
        WebPageObject webPageObject = parseSourceData(googleSearchResultItem);
        if (ConstsStrings.CONNECTION_ISSUE.equalsIgnoreCase(googleSearchResultItem.getLink())) {
            String error = ConstsStrings.CONNECTION_ISSUE + " - Reason: " + googleSearchResultItem.getDescription();
            if (error.contains("429 Too Many Request")) {
                error = ConstsStrings.ERROR_TOO_MANY_REQUEST;
            }
            return Optional.of(new OutputRegularCSVItem(googleSearchResultItem.getMainHeader(), "", googleSearchResultItem.getMainHeader(), "", "", error, ""));

        } else if (specification.isSatisfiedBy(webPageObject)) {
            String companyName = getCompanyName(webPageObject, googleSearchResultItem);
            String webSite = getWebSite(googleSearchResultItem);
            String htmlPageTitle = getHtmlPageTitle(webPageObject, googleSearchResultItem);
            boolean isAlive = new ProxyWebClient(diResolver).isWebsiteAlive(webSite);
            return Optional.of(new OutputRegularCSVItem(companyName, webSite, htmlPageTitle, "", "", "", !isAlive ? "Broken" : ""));
        } else {
            return Optional.of(new OutputRegularCSVItem(googleSearchResultItem.getMainHeader(), "", googleSearchResultItem.getMainHeader(), "", "", ConstsStrings.RESULT_NOT_MATCHED, ""));
        }
    }

    private WebPageObject parseSourceData(GoogleSearchResultItem googleSearchResultItem) {
        return new WebPageObject(googleSearchResultItem.getDescription(),
                googleSearchResultItem.getDescription(),
                googleSearchResultItem.getMainHeader(),
                googleSearchResultItem.getDescription(),
                googleSearchResultItem.getLink());

    }

    private String getHtmlPageTitle(WebPageObject webPageObject, GoogleSearchResultItem googleSearchResultItem) {
        if (StringUtils.isEmpty(webPageObject.getSiteName())) {
            return googleSearchResultItem.getMainHeader();
        }
        return webPageObject.getSiteName();
    }

    private String getWebSite(GoogleSearchResultItem googleSearchResultItem) {
        return StrUtils.extractDomainName(googleSearchResultItem.getLink());
    }
}
