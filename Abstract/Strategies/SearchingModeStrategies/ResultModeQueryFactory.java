package Abstract.Strategies.SearchingModeStrategies;

import Services.DIResolver;

public class ResultModeQueryFactory {

    public static QueryStrategy getQueryMode(DIResolver diResolver) {
        if (diResolver.getDbConnectionService().getExportMatchingDomain()) {
            return new DomainMatchingQueryStrategy(diResolver);
        }
        return new SocialProfileQueryStrategy(diResolver);
    }
}
