package Abstract.Strategies.SearchingModeStrategies;

import Abstract.Models.RequestData;
import Abstract.Models.SearchResultModels.GoogleSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import Abstract.Specifications.AbstractSpecification;

import java.util.Set;
import java.util.function.Consumer;

public abstract class QueryStrategy {

    public abstract int query(RequestData requestData, AbstractSpecification<GoogleSearchResultItem> settingSpec, Consumer<Set<RegularSearchResultItem>> searchResults);
}
