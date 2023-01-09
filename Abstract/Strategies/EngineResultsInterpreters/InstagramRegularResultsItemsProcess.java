package Abstract.Strategies.EngineResultsInterpreters;

import Abstract.Models.SearchResultModels.SocialSearchResultItem;
import Abstract.Models.SearchResultModels.RegularSearchResultItem;
import org.jsoup.nodes.Element;

import java.util.List;

public class InstagramRegularResultsItemsProcess extends RegularResultsItemsProcess {
    @Override
    public List<RegularSearchResultItem> translateDuckDuckGoBodyToModel(Element body) {
        return super.translateDuckDuckGoBodyToModel(body);
    }

    @Override
    public List<RegularSearchResultItem> translateStartPageBodyToModel(Element body) {
        return super.translateStartPageBodyToModel(body);
    }

    @Override
    public List<RegularSearchResultItem> translateBodyToModels(Element body) {
        return super.translateBodyToModels(body);
    }


    @Override
    protected RegularSearchResultItem createRegularSearchResultItem(String mainHeader, String link, String description) {
        return new SocialSearchResultItem(mainHeader, link, description);
    }

    @Override
    protected String cleanLink(String rawLink) {
        return rawLink;
    }
}
