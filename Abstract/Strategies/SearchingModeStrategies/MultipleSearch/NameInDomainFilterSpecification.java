package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.StrUtils;
import org.apache.commons.lang.StringUtils;
import org.tinylog.Logger;

import java.util.*;
import java.util.regex.Pattern;

public abstract class NameInDomainFilterSpecification extends AbstractSpecification<InputCsvModelItem> {
    private List<String> specificWordInDomains;
    private Set<String> ignoreTexts;

    public NameInDomainFilterSpecification(List<String> specificWordInDomains, Collection<String> ingoreTexts) {
        this.specificWordInDomains = specificWordInDomains;
        this.ignoreTexts = new HashSet<>(ingoreTexts);
    }

    protected abstract Pattern getAcceptedPattern();

    @Override
    public boolean isSatisfiedBy(InputCsvModelItem inputCsvModelItem) {
        Pattern pattern = getAcceptedPattern();
        List<String> skipSearchTexts = new ArrayList<>();
        for (int i = 0; i < specificWordInDomains.size(); i++) {
            String searchKey = specificWordInDomains.get(i);
            String placeHolder = StrUtils.replacePlaceholderTermsToData(inputCsvModelItem, searchKey);
            if (StringUtils.isBlank(placeHolder)) {
                skipSearchTexts.add(placeHolder);
                continue;
            }
            if (ignoreTexts.contains(placeHolder.trim())) {
                skipSearchTexts.add(placeHolder);

                continue;
            }
            String[] inputs = placeHolder.split("\\s");
            if (Arrays.stream(inputs).noneMatch(x -> pattern.matcher(x).matches()) || inputs.length > maxSpaceInName()) {
                skipSearchTexts.add(placeHolder);

            }

        }
        if (skipSearchTexts.size() == specificWordInDomains.size()) {
            Logger.tag("SYSTEM").info("Skip search, " + StringUtils.join(skipSearchTexts, ","));
            return false;
        }
        return true;
    }

    protected int maxSpaceInName() {
        return 5;
    }
}
