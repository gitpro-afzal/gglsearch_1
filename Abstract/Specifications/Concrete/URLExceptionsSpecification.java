package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;
import java.util.ArrayList;

public class URLExceptionsSpecification extends AbstractSpecification<GoogleSearchResultItem> {

    private ArrayList<String> URLExceptions;
    private InputCsvModelItem inputCsvModelItem;
    public URLExceptionsSpecification(ArrayList<String> URLExceptions, InputCsvModelItem inputCsvModelItem) {
        this.URLExceptions = URLExceptions;
        this.inputCsvModelItem = inputCsvModelItem;
    }

    @Override
    public boolean isSatisfiedBy(GoogleSearchResultItem googleSearchResultItem) {
        if (URLExceptions.size() == 0) {
            return true;
        }
        String str = StrUtils.getUnmatchedPartOfString(googleSearchResultItem.getLink());
        for (String urlException: URLExceptions) {
            if (StrUtils.isPlaceholderHasSubstituteTerms(urlException)) {
                urlException = StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, urlException);
            }
            if (str.toLowerCase().contains(urlException.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
