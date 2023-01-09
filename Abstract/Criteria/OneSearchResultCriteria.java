package Abstract.Criteria;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class OneSearchResultCriteria implements Criteria<RegularSearchResultItem> {

    private final String[] searchPlaceHolder;
    private InputCsvModelItem inputCsvModelItem;
    private final List<String> matchingNames = new ArrayList<>();
    private ArrayList<String> textInSearch = new ArrayList<>();

    public OneSearchResultCriteria(String searchPlaceholder, InputCsvModelItem inputCsvModelItem) {
        this.inputCsvModelItem = inputCsvModelItem;
        if (this.inputCsvModelItem == null) {
            this.inputCsvModelItem = new InputCsvModelItem();
            this.searchPlaceHolder = searchPlaceholder.trim().split("\\s+");
        } else {
            List<String> searchColumns = StrUtils.extractSearchHolderColumns(searchPlaceholder);
            searchColumns.forEach(column -> {
                if (StrUtils.isPlaceholderHasSubstituteTerms(column)) {
                    textInSearch.add(StrUtils.replacePlaceholderTermsToData(inputCsvModelItem, column));
                } else {
                    textInSearch.add(column);
                }
            });

            this.searchPlaceHolder = textInSearch.toArray(new String[textInSearch.size()]);
        }

        initMatchingNames();
    }

    private void initMatchingNames() {
        if (this.textInSearch.size() > 0) {
            if (this.textInSearch.size() <= 2) {
                matchingNames.addAll(textInSearch);
            } else {
                for (int i = 0; i < textInSearch.size() - 1; i++) {
                    matchingNames.add(textInSearch.get(i));
                }
            }
        } else {
            if (searchPlaceHolder.length == 1) {
                matchingNames.add(searchPlaceHolder[0]);
            } else if (searchPlaceHolder.length == 2) {
                matchingNames.add(searchPlaceHolder[0]);
                matchingNames.add(searchPlaceHolder[1]);
            } else {
                matchingNames.add(searchPlaceHolder[1]);
                matchingNames.add(searchPlaceHolder[2]);
            }
        }
    }

    @Override
    public List<RegularSearchResultItem> meetCriteria(List<RegularSearchResultItem> results) {
        List<RegularSearchResultItem> resultItems = new ArrayList<>();
        if (results == null || results.size() == 0) {
            return resultItems;
        }

        for (RegularSearchResultItem regularSearchResultItem : results) {
            if (isSatisfiedByEntries(regularSearchResultItem)) {
                resultItems.add(regularSearchResultItem);
            }
        }
        return resultItems;
    }

    private boolean isSatisfiedByEntries(RegularSearchResultItem regularSearchResultItem) {
        if (regularSearchResultItem == null) {
            return false;
        }
        return isContainsLogic(regularSearchResultItem);
    }

    private boolean isContainsLogic(RegularSearchResultItem regularSearchResultItem) {
        regularSearchResultItem.setScore(StrUtils.getMaxScore(matchingNames, regularSearchResultItem.getHostName()));
        return regularSearchResultItem.getScore() >= 0.8;
    }

}
