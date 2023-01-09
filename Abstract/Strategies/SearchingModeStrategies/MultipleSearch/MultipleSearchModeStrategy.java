package Abstract.Strategies.SearchingModeStrategies.MultipleSearch;

import Abstract.Engines.InstagramClient;
import Abstract.Engines.StartPageContext;
import Abstract.Engines.TorProxyContext;
import Abstract.Exceptions.InputFileEmptyException;
import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.RequestData;
import Abstract.Specifications.AbstractSpecification;
import Abstract.Strategies.SearchModeStrategyBase;
import Abstract.Tasks.Worker;
import Services.DIResolver;
import Utils.StrUtils;
import org.apache.commons.lang.StringUtils;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MultipleSearchModeStrategy extends SearchModeStrategyBase {

    private AtomicInteger googleFailedCount = new AtomicInteger();

    public MultipleSearchModeStrategy(DIResolver diResolver) {
        super(diResolver);
    }

    @Override
    public void processData() throws InputFileEmptyException {
        StartPageContext.settingPages();
        SocialCacheResult.clearCache();
        TorProxyContext.initSockContext(diResolver.getDbConnectionService().getProxyHostProperty(),
                diResolver.getDbConnectionService().getProxyPortProperty());
        InstagramClient.initSockContext(diResolver.getDbConnectionService().getProxyHostProperty(),
                diResolver.getDbConnectionService().getProxyPortProperty());
        diResolver.getDbConnectionService().updateWorkStatus(true);
        diResolver.getGuiService().setStatusText("Processing started");
        List<InputCsvModelItem> csvFileData = diResolver.getInputDataService().getInputCsvModelItems();
        int size = diResolver.getInputDataService().getInputCsvModelItems().size();

        if (size == 0) {
            throw new InputFileEmptyException("Input data file doesn't contain elements");
        }

        if (!diResolver.getDbConnectionService().getGoogleSearchEngine() && !diResolver.getDbConnectionService().getGoogleMapsEngine()) {
            diResolver.getGuiService().changeApplicationStateToWork(false);
            Logger.tag("SYSTEM").info("No search method selected");
            diResolver.getGuiService().setStatusText("No search method selected");
            return;
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        int count = 0;
        List<String> specificWordInDomain = diResolver.getDbConnectionService().getSearchSettings().KeywordsForLookingInDomainURLs;
        if (specificWordInDomain.isEmpty()) {
            specificWordInDomain = StrUtils.extractSearchHolderColumns(diResolver.getGuiService().getSearchPlaceholderText());
        }

        AbstractSpecification<InputCsvModelItem> filterInputSpecification;
        if (diResolver.getDbConnectionService().getSearchEmailProperty()) {
            filterInputSpecification = new EmailFilterSpecification(specificWordInDomain);
        } else if (diResolver.getDbConnectionService().getSearchPersonNameProperty()) {
            filterInputSpecification = new PersonNameInDomainFilterSpecification(specificWordInDomain,
                    diResolver.getDbConnectionService().getSearchSettings().IgnoreSearchTexts);
        } else {
            filterInputSpecification = new CompanyNameInDomainFilterSpecification(specificWordInDomain,
                    diResolver.getDbConnectionService().getSearchSettings().IgnoreSearchTexts);
        }
        int skipCount = 0;

        csvFileData = combineColumns(csvFileData);
        String searchPattern = diResolver.getGuiService().getSearchPlaceholderText();
        for (int i = 0; i < csvFileData.size(); i++) {
            InputCsvModelItem item = csvFileData.get(i);
            if (!filterInputSpecification.isSatisfiedBy(item)) {
                skipCount++;
                continue;
            }

            count++;
            String URL = StrUtils.createUrlForMultipleSearch(item, searchPattern);
            String requestTerm = StrUtils.createSearchTermForMultipleSearch(item, searchPattern);
            RequestData requestData = new RequestData(URL, 3, getRandomNumberInRange(4000, 8000), item);
            requestData.setRequestTerm(StrUtils.encodeStringToUTF8(requestTerm));
            requestData.requestStartPageBody = StrUtils.createQueryForStartPageSearch(item, searchPattern);
            Runnable worker = new Worker(diResolver, requestData, googleFailedCount);
            executor.execute(worker);

        }
        Logger.tag("SYSTEM").info("Skip [" + skipCount + "] dirty records ");
        waitForTheEnd(count);
    }

    private List<InputCsvModelItem> combineColumns(List<InputCsvModelItem> csvFileData) {
        String searchPattern = diResolver.getGuiService().getSearchPlaceholderText();
        Set<InputCsvModelItem> csvItems = new TreeSet<>(Comparator.comparingInt(InputCsvModelItem::hashCode));
        if (diResolver.getDbConnectionService().getCombineSearchColumnsProperty()) {
            List<String> placeHolderColumns = StrUtils.extractSearchHolderColumns(searchPattern)
                    .stream()
                    .map(x -> x.replaceAll("[{}]", ""))
                    .collect(Collectors.toList());
            for (int i = 0; i < csvFileData.size(); i++) {
                InputCsvModelItem item = csvFileData.get(i);
                // csvItems.add(item);
                Map<String, String> itemMap = StrUtils.valuesMap(item);
                if (placeHolderColumns.stream()
                        .allMatch(c -> itemMap.entrySet().stream()
                                .anyMatch(entry -> entry.getKey().equalsIgnoreCase(c)
                                        && StringUtils.isNotBlank(entry.getValue())))) {
                    csvItems.add(item);

                }
                for (String columnName : placeHolderColumns) {
                    String value = itemMap.get(columnName.toLowerCase());
                    if (StringUtils.isBlank(value)) continue;

                    for (InputCsvModelItem desItem : csvFileData) {
                        Map<String, String> desItemMap = StrUtils.valuesMap(desItem);
                        if (placeHolderColumns.stream()
                                .allMatch(c -> desItemMap.entrySet().stream()
                                        .anyMatch(entry -> entry.getKey().equalsIgnoreCase(c)
                                                && StringUtils.isNotBlank(entry.getValue())))) {
                            InputCsvModelItem copiedItem = desItem.copy();

                            copiedItem.setValue(columnName, value);
                            csvItems.add(copiedItem);
                        }
                    }

                }

            }
            return new ArrayList<>(csvItems);

        }
        return csvFileData;
    }

    public void stopProcessing() {
        executor.shutdown();
        Thread thread = new Thread(() -> {
            try {
                while (!executor.isTerminated()) {
                    Thread.sleep(1000);
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Logger.error(e);
                Logger.tag("SYSTEM").error(e);
            }
            diResolver.getGuiService().changeApplicationStateToWork(false);
            Logger.tag("SYSTEM").info("Finished");
            diResolver.getGuiService().setStatusText("Finished");
        });
        thread.start();
    }

    @Override
    public void updateStatusText() {
        diResolver.getGuiService().updateCountItemsStatus((int) executor.getCompletedTaskCount(), (int) executor.getTaskCount());
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
