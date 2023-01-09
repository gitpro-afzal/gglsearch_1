package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class CompanyNameInDomainFilterSpecification extends NameInDomainFilterSpecification {
    private static Pattern namePattern = Pattern.compile("[^\\p{Punct}^[,.]]+");

    public CompanyNameInDomainFilterSpecification(List<String> specificWordInDomains, Collection<String> ingoreTexts) {
        super(specificWordInDomains, ingoreTexts);
    }

    @Override
    protected Pattern getAcceptedPattern() {
        return namePattern;
    }

    protected int maxSpaceInName() {
        return 10; // avoid search paragraph or sentence
    }

}
