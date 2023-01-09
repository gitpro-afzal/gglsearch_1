package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Models.SearchResultModels.RegularSearchResultItem;

import java.util.*;

public class SocialCacheResult {
    private static Set<RegularSearchResultItem> socialOutputs = Collections.synchronizedSet(new HashSet<RegularSearchResultItem>());

    public static List<RegularSearchResultItem> addAll(Collection<RegularSearchResultItem> results) {
        List<RegularSearchResultItem> nonDuplicatedRecords = new ArrayList<>();
        results.forEach(result -> {
            if (socialOutputs.add(result)) {
                nonDuplicatedRecords.add(result);
            }
        });
        return nonDuplicatedRecords;
    }

    public static void clearCache() {
        socialOutputs.clear();
    }
}
