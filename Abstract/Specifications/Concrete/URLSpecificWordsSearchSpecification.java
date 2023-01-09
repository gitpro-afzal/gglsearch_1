package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;

import java.util.*;
import java.util.stream.Collectors;

public class URLSpecificWordsSearchSpecification extends AbstractSpecification<GoogleSearchResultItem> {

    private ArrayList<String> URLSpecificWords;
    private InputCsvModelItem inputCsvModelItem;

    public URLSpecificWordsSearchSpecification(ArrayList<String> URLSpecificWords, InputCsvModelItem inputCsvModelItem) {
        this.inputCsvModelItem = inputCsvModelItem;
        this.URLSpecificWords = URLSpecificWords;
    }

    @Override
    public boolean isSatisfiedBy(GoogleSearchResultItem googleSearchResultItem) {
        if (URLSpecificWords.size() == 0) {
            return true;
        }

        List<String> searchTexts= URLSpecificWords.stream().map(urlSpecificWords -> {
            if (StrUtils.isPlaceholderHasSubstituteTerms(urlSpecificWords)) {
                return Arrays.stream(StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, urlSpecificWords).split("\\s")).collect(Collectors.toList());
            }
            return Collections.singleton(urlSpecificWords);
        }).flatMap(Collection::stream).collect(Collectors.toList());

        return StrUtils.isMatchedByDistance(searchTexts, googleSearchResultItem.getHostName());
    }
}
