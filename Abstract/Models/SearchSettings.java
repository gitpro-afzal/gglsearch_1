package Abstract.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchSettings {
    public static Set<String> DEFAULT_ACCEPTED_GUESSED_DOMAINS = Arrays.stream(new String[]{"gmail.com", "yahoo.com", "hotmail.com", "outlook.com"}).collect(Collectors.toSet());
    public ArrayList<String> ExceptionsForFoundDomains;
    public ArrayList<String> ExceptionsForWordsInDomainURLs;
    public ArrayList<String> MetaTagsExceptions;
    public ArrayList<String> ExceptionsForTopLevelDomains;
    public ArrayList<String> KeywordsForLookingInSearchResults;
    public ArrayList<String> KeywordsForLookingInDomainURLs;
    public Set<String> IgnoreSearchTexts = new HashSet<>();
    public Set<String> AcceptedGuessedDomainEmails = new HashSet<>();

    public SearchSettings() {
        ExceptionsForFoundDomains = new ArrayList<>();
        ExceptionsForWordsInDomainURLs = new ArrayList<>();
        MetaTagsExceptions = new ArrayList<>();
        ExceptionsForTopLevelDomains = new ArrayList<>();
        KeywordsForLookingInSearchResults = new ArrayList<>();
        KeywordsForLookingInDomainURLs = new ArrayList<>();
    }

    public void resetAcceptedGuessedDomainEmails() {
        AcceptedGuessedDomainEmails = DEFAULT_ACCEPTED_GUESSED_DOMAINS;
    }
}
