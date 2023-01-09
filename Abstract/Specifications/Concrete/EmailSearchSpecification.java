package Abstract.Specifications.Concrete;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Specifications.AbstractSpecification;
import Utils.ConstsStrings;
import Utils.StrUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmailSearchSpecification extends AbstractSpecification<GoogleSearchResultItem> {


    private Pattern emailPattern = Pattern.compile(ConstsStrings.EMAIL_REGEX);
    private Pattern domainPattern = Pattern.compile("([^\\p{Punct}^[-._]]+)([.])([\\w]{2,6}$)");
    private List<String> searchTexts = new ArrayList<>();
    private Set<String> acceptedGuessedDomains;

    public EmailSearchSpecification(List<String> urlSpecificWords, Set<String> acceptedGuessedDomains, InputCsvModelItem inputCsvModelItem) {
        this.acceptedGuessedDomains = acceptedGuessedDomains;
        this.searchTexts = urlSpecificWords.stream().map(specificWords -> {
            if (StrUtils.isPlaceholderHasSubstituteTerms(specificWords)) {
                return Arrays.stream(
                        cleanProtocolSchema(StrUtils.createSearchTermForMultipleSearch(inputCsvModelItem, specificWords))
                                .split("\\s")).collect(Collectors.toList());
            }
            return Collections.singleton(cleanProtocolSchema(specificWords));
        }).flatMap(Collection::stream).collect(Collectors.toList());

    }

    private String cleanProtocolSchema(String text) {
        return text.replace("\"", "")
                .replaceAll("http://www\\.|https://www\\.|http://|https://|www.", "");
    }

    @Override
    public boolean isSatisfiedBy(GoogleSearchResultItem googleSearchResultItem) {
        if (StringUtils.isBlank(googleSearchResultItem.getDescription())) {
            return false;
        }
        Matcher matcher = emailPattern.matcher(googleSearchResultItem.getDescription());
        boolean isEmailMatched = false;
        while (matcher.find()) {
            String email = matcher.group().toLowerCase();
            boolean isMatched = false;
            for (String text : searchTexts) {
                String searchText = text.toLowerCase();
                if (domainPattern.matcher(searchText).matches() && email.endsWith(searchText)) {
                    isMatched = isEmailMatched = true;
                    googleSearchResultItem.getMetadatas().add(new GoogleSearchResultItem.Metadata(googleSearchResultItem.getMainHeader(), googleSearchResultItem.getLink(), email, ConstsStrings.EMAIL_STATUS_SAME_DOMAIN));
                    break;
                }
            }
            if (isMatched) {
                continue;
            }
            if (StrUtils.isMatchedByDistance(searchTexts, email) && acceptedGuessedDomains.stream().anyMatch(email::endsWith)) {
                isEmailMatched = true;
                googleSearchResultItem.getMetadatas().add(new GoogleSearchResultItem.Metadata(googleSearchResultItem.getMainHeader(), googleSearchResultItem.getLink(), email, ConstsStrings.EMAIL_STATUS_GUESSED_DOMAIN));
                continue;
            }

        }
        return isEmailMatched;
    }
}
