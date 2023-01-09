package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Models.SearchResultModels.WebPageObject;
import Utils.StrUtils;

import java.util.ArrayList;

public class MetaTagsExceptionsSpecification extends AbstractSpecification<WebPageObject> {

    private ArrayList<String> metaTagsExceptions;
    private InputCsvModelItem inputCsvModelItem;
    public MetaTagsExceptionsSpecification(ArrayList<String> metaTagsExceptions, InputCsvModelItem inputCsvModelItem){
        this.metaTagsExceptions = metaTagsExceptions;
        this.inputCsvModelItem = inputCsvModelItem;
    }

    @Override
    public boolean isSatisfiedBy(WebPageObject webPageObject) {
        if (metaTagsExceptions.size() == 0) {
            return true;
        }
        for (String metaExceptionKeyword : metaTagsExceptions) {
            if (StrUtils.isPlaceholderHasSubstituteTerms(metaExceptionKeyword)) {
                metaExceptionKeyword = StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, metaExceptionKeyword);
            }
            if (webPageObject.getSiteDescription().toLowerCase().contains(metaExceptionKeyword.toLowerCase())) {
                return false;
            }
            if (webPageObject.getSiteKeywords().toLowerCase().contains(metaExceptionKeyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
