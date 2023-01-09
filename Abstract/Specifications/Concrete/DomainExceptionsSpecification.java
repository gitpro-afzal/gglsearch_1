package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;
import java.util.ArrayList;

public class DomainExceptionsSpecification extends AbstractSpecification<GoogleSearchResultItem> {

    private ArrayList<String> domainExceptions;
    private InputCsvModelItem  inputCsvModelItem;
    public DomainExceptionsSpecification(ArrayList<String> domainExceptions, InputCsvModelItem inputCsvModelItem) {
        this.domainExceptions = domainExceptions;
        this.inputCsvModelItem = inputCsvModelItem;
    }

    @Override
    public boolean isSatisfiedBy(GoogleSearchResultItem googleSearchResultItem) {
        if (domainExceptions.size() == 0) {
            return true;
        }
        String domainName = StrUtils.extractDomainName(googleSearchResultItem.getLink());
        for (String domainNameException: domainExceptions) {
            if (StrUtils.isPlaceholderHasSubstituteTerms(domainNameException)) {
                domainNameException = StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, domainNameException);
            }
            if (domainName.toLowerCase().contains(domainNameException.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
