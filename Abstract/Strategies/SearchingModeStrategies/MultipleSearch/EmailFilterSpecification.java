package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Specifications.AbstractSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmailFilterSpecification extends AbstractSpecification<InputCsvModelItem> {
    private Pattern domainPattern = Pattern.compile("^([a-z0-9@ ]+(-[a-z0-9]+)*\\.)+[a-z]{2,}$");
    private List<String> specificWordInDomains = new ArrayList<>();

    public EmailFilterSpecification(List<String> specificWordInDomains) {
        this.specificWordInDomains = specificWordInDomains;
    }

    @Override
    public boolean isSatisfiedBy(InputCsvModelItem inputCsvModelItem) {
//        for (String searchKey : specificWordInDomains) {
//            String placeHolder = StrUtils.replacePlaceholderTermsToData(inputCsvModelItem, searchKey);
//            if (StringUtils.isBlank(placeHolder)) {
//                return false;
//            }
//            if (!domainPattern.matcher(placeHolder).matches()) {
//                return false;
//            }
//        }
        return true;
    }
}
