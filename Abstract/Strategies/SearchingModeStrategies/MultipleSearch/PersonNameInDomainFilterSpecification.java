package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public  class PersonNameInDomainFilterSpecification extends NameInDomainFilterSpecification {
    private static Pattern namePattern = Pattern.compile("[a-zA-Z-. ]+");

    public PersonNameInDomainFilterSpecification(List<String> specificWordInDomains, Collection<String> ingoreTexts) {
        super(specificWordInDomains, ingoreTexts);
    }

    @Override
    protected  Pattern getAcceptedPattern() {
        return namePattern;
    }
}
