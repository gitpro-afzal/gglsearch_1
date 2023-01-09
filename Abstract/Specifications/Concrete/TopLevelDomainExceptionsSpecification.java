package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;

import java.util.ArrayList;

public class TopLevelDomainExceptionsSpecification extends AbstractSpecification<GoogleSearchResultItem> {

    private ArrayList<String> topLevelDomainsExceptions;
    private InputCsvModelItem inputCsvModelItem;
    public TopLevelDomainExceptionsSpecification(ArrayList<String> topLevelDomainsExceptions, InputCsvModelItem inputCsvModelItem) {
        this.topLevelDomainsExceptions = topLevelDomainsExceptions;
        this.inputCsvModelItem = inputCsvModelItem;
    }

    @Override
    public boolean isSatisfiedBy(GoogleSearchResultItem googleSearchResultItem) {
        if (topLevelDomainsExceptions.size() == 0) {
            return true;
        }
        for (String topLevelDomainException: topLevelDomainsExceptions) {
            if (StrUtils.isPlaceholderHasSubstituteTerms(topLevelDomainException)) {
                topLevelDomainException = StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, topLevelDomainException);
            }
            if (googleSearchResultItem.getLink().toLowerCase().contains(topLevelDomainException.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
