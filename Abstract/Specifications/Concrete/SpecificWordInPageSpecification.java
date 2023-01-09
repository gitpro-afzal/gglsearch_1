package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.WebPageObject;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;

import java.util.ArrayList;

public class SpecificWordInPageSpecification extends AbstractSpecification<WebPageObject> {

    private ArrayList<String> specificWordsToSearch;
    private  InputCsvModelItem inputCsvModelItem;
    public SpecificWordInPageSpecification(ArrayList<String> specificWordsToSearch, InputCsvModelItem inputCsvModelItem){
        this.specificWordsToSearch = specificWordsToSearch;
        this.inputCsvModelItem = inputCsvModelItem;
    }

    @Override
    public boolean isSatisfiedBy(WebPageObject webPageObject) {
        if (specificWordsToSearch.size() == 0) {
            return true;
        }

        for (String specificWord : specificWordsToSearch) {
            if (StrUtils.isPlaceholderHasSubstituteTerms(specificWord)) {
                specificWord = StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, specificWord);
            }
            if (webPageObject.getPagePlainText().toLowerCase().contains(specificWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
