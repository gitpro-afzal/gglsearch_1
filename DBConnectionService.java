package Services;

import Abstract.Models.Database.DAO.AppSettingsDao;
import Abstract.Models.Database.DAO.SearchSettingsDao;
import Abstract.Models.Database.Entities.ApplicationSettingsEntity;
import Abstract.Models.Database.Entities.SearchSettingsEntity;
import Abstract.Models.SearchSettings;
import Utils.PropertyKeys;
import Utils.StrUtils;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.lang.StringUtils;
import org.tinylog.Logger;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBConnectionService {

    private static JdbcPooledConnectionSource connectionSource;

    public DBConnectionService() {
        try {
            String dbPath = "jdbc:h2:"
                    + new File(DBConnectionService.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsoluteFile().getParent() + File.separator + "gglSearcher"
                    + File.separator + "gglSearcherDb";
            Logger.tag("SYSTEM").info(dbPath);
            connectionSource = new JdbcPooledConnectionSource(dbPath);
            createTablesIfNotExists();
            initFillSearchSettingsTable();
            initFillApplicationSettingsTable();
        } catch (Exception e) {
            Logger.tag("SYSTEM").error(e);
        }
    }

    private void createTablesIfNotExists() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, SearchSettingsEntity.class);
        TableUtils.createTableIfNotExists(connectionSource, ApplicationSettingsEntity.class);
    }

    private void initFillSearchSettingsTable() throws SQLException {
        SearchSettingsDao searchSettingsDao = DaoManager.createDao(connectionSource, SearchSettingsEntity.class);

        SearchSettingsEntity SpecificWordsInDomainURLs = new SearchSettingsEntity();
        SpecificWordsInDomainURLs.setSettingName(PropertyKeys.SpecificWordsInDomainURLs);
        searchSettingsDao.createIfNotExists(SpecificWordsInDomainURLs);

        SearchSettingsEntity MetaTagsExceptions = new SearchSettingsEntity();
        MetaTagsExceptions.setSettingName(PropertyKeys.MetaTagsExceptions);
        searchSettingsDao.createIfNotExists(MetaTagsExceptions);

        SearchSettingsEntity DomainExceptions = new SearchSettingsEntity();
        DomainExceptions.setSettingName(PropertyKeys.DomainExceptions);
        searchSettingsDao.createIfNotExists(DomainExceptions);

        SearchSettingsEntity TopLevelDomainsExceptions = new SearchSettingsEntity();
        TopLevelDomainsExceptions.setSettingName(PropertyKeys.TopLevelDomainsExceptions);
        searchSettingsDao.createIfNotExists(TopLevelDomainsExceptions);

        SearchSettingsEntity KeywordsInSearchResults = new SearchSettingsEntity();
        KeywordsInSearchResults.setSettingName(PropertyKeys.KeywordsInSearchResults);
        searchSettingsDao.createIfNotExists(KeywordsInSearchResults);

        SearchSettingsEntity URLExceptions = new SearchSettingsEntity();
        URLExceptions.setSettingName(PropertyKeys.URLExceptions);
        searchSettingsDao.createIfNotExists(URLExceptions);

        SearchSettingsEntity URLSExclusions = new SearchSettingsEntity();
        URLSExclusions.setSettingName(PropertyKeys.URLsExclusions);
        searchSettingsDao.createIfNotExists(URLSExclusions);

        SearchSettingsEntity ignoreSearchTexts = new SearchSettingsEntity();
        ignoreSearchTexts.setSettingName(PropertyKeys.IgnoreSearchTexts);
        searchSettingsDao.createIfNotExists(ignoreSearchTexts);

        SearchSettingsEntity acceptedGuessedDomains = new SearchSettingsEntity();
        acceptedGuessedDomains.setSettingName(PropertyKeys.AcceptedGuessedDomains);
        acceptedGuessedDomains.setSettingValue(String.join(StrUtils.newLineDelimiter, SearchSettings.DEFAULT_ACCEPTED_GUESSED_DOMAINS));
        searchSettingsDao.createIfNotExists(acceptedGuessedDomains);

    }

    private void initFillApplicationSettingsTable() throws SQLException {
        AppSettingsDao appSettingsDao = DaoManager.createDao(connectionSource, ApplicationSettingsEntity.class);

        ApplicationSettingsEntity workStatus = new ApplicationSettingsEntity();
        workStatus.setSettingName(PropertyKeys.WorkStatus);
        appSettingsDao.createIfNotExists(workStatus);

        ApplicationSettingsEntity dataFilePath = new ApplicationSettingsEntity();
        dataFilePath.setSettingName(PropertyKeys.DataFilePath);
        appSettingsDao.createIfNotExists(dataFilePath);

        ApplicationSettingsEntity searchPlaceholder = new ApplicationSettingsEntity();
        searchPlaceholder.setSettingName(PropertyKeys.SearchPlaceholder);
        appSettingsDao.createIfNotExists(searchPlaceholder);

        ApplicationSettingsEntity SearchInGoogleEngine = new ApplicationSettingsEntity();
        SearchInGoogleEngine.setSettingName(PropertyKeys.SearchInGoogleEngine);
        SearchInGoogleEngine.setSettingValue("true");
        appSettingsDao.createIfNotExists(SearchInGoogleEngine);

        ApplicationSettingsEntity SearchInGoogleMapsEngine = new ApplicationSettingsEntity();
        SearchInGoogleMapsEngine.setSettingName(PropertyKeys.SearchInGoogleMapsEngine);
        appSettingsDao.createIfNotExists(SearchInGoogleMapsEngine);

        ApplicationSettingsEntity searchPersonName = new ApplicationSettingsEntity();
        searchPersonName.setSettingName(PropertyKeys.SearchPersonName);
        appSettingsDao.createIfNotExists(searchPersonName);

        ApplicationSettingsEntity combineSearchColumns = new ApplicationSettingsEntity();
        combineSearchColumns.setSettingName(PropertyKeys.CombineSearchColumns);
        appSettingsDao.createIfNotExists(combineSearchColumns);

        ApplicationSettingsEntity singleSearch = new ApplicationSettingsEntity();
        singleSearch.setSettingName(PropertyKeys.SingleSearch);
        appSettingsDao.createIfNotExists(singleSearch);

        ApplicationSettingsEntity searchDomain = new ApplicationSettingsEntity();
        searchDomain.setSettingName(PropertyKeys.SearchEmail);
        appSettingsDao.createIfNotExists(searchDomain);

        ApplicationSettingsEntity retainOriginCsvHeader = new ApplicationSettingsEntity();
        retainOriginCsvHeader.setSettingName(PropertyKeys.RetainOrginCsvHeader);
        retainOriginCsvHeader.setSettingValue("true");
        appSettingsDao.createIfNotExists(retainOriginCsvHeader);

        ApplicationSettingsEntity proxyHost = new ApplicationSettingsEntity();
        proxyHost.setSettingName(PropertyKeys.ProxyHost);
        proxyHost.setSettingValue("78.47.115.35");
        appSettingsDao.createIfNotExists(proxyHost);

        ApplicationSettingsEntity proxyPort = new ApplicationSettingsEntity();
        proxyPort.setSettingName(PropertyKeys.ProxyPort);
        proxyPort.setSettingValue("9050");
        appSettingsDao.createIfNotExists(proxyPort);

        ApplicationSettingsEntity exportMatchingDomain = new ApplicationSettingsEntity();
        exportMatchingDomain.setSettingName(PropertyKeys.ExportMatchingDomain);
        exportMatchingDomain.setSettingValue("true");
        appSettingsDao.createIfNotExists(exportMatchingDomain);

        /*Columns*/
        ApplicationSettingsEntity columnA = new ApplicationSettingsEntity();
        columnA.setSettingName(PropertyKeys.columnA);
        appSettingsDao.createIfNotExists(columnA);

        ApplicationSettingsEntity columnB = new ApplicationSettingsEntity();
        columnB.setSettingName(PropertyKeys.columnB);
        appSettingsDao.createIfNotExists(columnB);

        ApplicationSettingsEntity columnC = new ApplicationSettingsEntity();
        columnC.setSettingName(PropertyKeys.columnC);
        appSettingsDao.createIfNotExists(columnC);

        ApplicationSettingsEntity columnD = new ApplicationSettingsEntity();
        columnD.setSettingName(PropertyKeys.columnD);
        appSettingsDao.createIfNotExists(columnD);

        ApplicationSettingsEntity columnE = new ApplicationSettingsEntity();
        columnE.setSettingName(PropertyKeys.columnE);
        appSettingsDao.createIfNotExists(columnE);

        ApplicationSettingsEntity columnF = new ApplicationSettingsEntity();
        columnF.setSettingName(PropertyKeys.columnF);
        appSettingsDao.createIfNotExists(columnF);

        ApplicationSettingsEntity columnG = new ApplicationSettingsEntity();
        columnG.setSettingName(PropertyKeys.columnG);
        appSettingsDao.createIfNotExists(columnG);

        ApplicationSettingsEntity columnH = new ApplicationSettingsEntity();
        columnH.setSettingName(PropertyKeys.columnH);
        appSettingsDao.createIfNotExists(columnH);

        ApplicationSettingsEntity columnI = new ApplicationSettingsEntity();
        columnI.setSettingName(PropertyKeys.columnI);
        appSettingsDao.createIfNotExists(columnI);

        ApplicationSettingsEntity columnJ = new ApplicationSettingsEntity();
        columnJ.setSettingName(PropertyKeys.columnJ);
        appSettingsDao.createIfNotExists(columnJ);

        ApplicationSettingsEntity columnK = new ApplicationSettingsEntity();
        columnK.setSettingName(PropertyKeys.columnK);
        appSettingsDao.createIfNotExists(columnK);

        ApplicationSettingsEntity columnL = new ApplicationSettingsEntity();
        columnL.setSettingName(PropertyKeys.columnL);
        appSettingsDao.createIfNotExists(columnL);

        ApplicationSettingsEntity columnM = new ApplicationSettingsEntity();
        columnM.setSettingName(PropertyKeys.columnM);
        appSettingsDao.createIfNotExists(columnM);

        ApplicationSettingsEntity columnN = new ApplicationSettingsEntity();
        columnN.setSettingName(PropertyKeys.columnN);
        appSettingsDao.createIfNotExists(columnN);

        ApplicationSettingsEntity columnO = new ApplicationSettingsEntity();
        columnO.setSettingName(PropertyKeys.columnO);
        appSettingsDao.createIfNotExists(columnO);
    }

    private void updateSearchSettingsPropertyByKey(PropertyKeys propertyKey, String value) {
        if (value == null || propertyKey == null) {
            return;
        }

        try {
            SearchSettingsDao searchSettingsDao = DaoManager.createDao(connectionSource, SearchSettingsEntity.class);
            SearchSettingsEntity settings = getSearchSettingsPropertyByKey(propertyKey);
            settings.setSettingName(propertyKey);
            settings.setSettingValue(value);

            searchSettingsDao.update(settings);
        } catch (SQLException e) {
            Logger.tag("SYSTEM").error(e, "Cannot update property: " + propertyKey);
        }
    }

    private void updateApplicationSettingsPropertyByKey(PropertyKeys propertyKey, String value) {
        if (value == null || propertyKey == null) {
            return;
        }

        try {
            AppSettingsDao searchSettingsDao = DaoManager.createDao(connectionSource, ApplicationSettingsEntity.class);
            ApplicationSettingsEntity settings = getApplicationSettingsPropertyByKey(propertyKey);
            settings.setSettingName(propertyKey);
            settings.setSettingValue(value);

            searchSettingsDao.update(settings);
        } catch (SQLException e) {
            Logger.tag("SYSTEM").error(e, "Cannot update property: " + propertyKey);
        }
    }

    private SearchSettingsEntity getSearchSettingsPropertyByKey(PropertyKeys propertyKey) {
        if (propertyKey == null) {
            return null;
        }
        SearchSettingsEntity settings = null;
        try {
            SearchSettingsDao searchSettingsDao = DaoManager.createDao(connectionSource, SearchSettingsEntity.class);
            settings = searchSettingsDao.findByKey(propertyKey).get(0);
            if (StringUtils.isEmpty(settings.getSettingValue())) {
                settings.setSettingValue("");
            }
        } catch (SQLException e) {
            Logger.tag("SYSTEM").error(e, "Cannot update property: " + propertyKey);
        }
        return settings;
    }

    private ApplicationSettingsEntity getApplicationSettingsPropertyByKey(PropertyKeys propertyKey) {
        if (propertyKey == null) {
            return null;
        }
        ApplicationSettingsEntity settings = null;
        try {
            AppSettingsDao appSettingsDao = DaoManager.createDao(connectionSource, ApplicationSettingsEntity.class);
            List<ApplicationSettingsEntity> allSettings = appSettingsDao.findByKey(propertyKey);
            if (allSettings.isEmpty()) {
                settings.setSettingValue("");
            } else {
                settings = appSettingsDao.findByKey(propertyKey).get(0);
                if (StringUtils.isEmpty(settings.getSettingValue())) {
                    settings.setSettingValue("");
                }
            }
        } catch (SQLException e) {
            Logger.tag("SYSTEM").error(e, "Cannot update property: " + propertyKey);
        }
        return settings;
    }

    public void saveSearchSettings(SearchSettings searchSettings) {
        try {
            updateSearchSettingsPropertyByKey(PropertyKeys.DomainExceptions, String.join(StrUtils.newLineDelimiter, searchSettings.ExceptionsForFoundDomains));
            updateSearchSettingsPropertyByKey(PropertyKeys.KeywordsInSearchResults, String.join(StrUtils.newLineDelimiter, searchSettings.KeywordsForLookingInSearchResults));
            updateSearchSettingsPropertyByKey(PropertyKeys.MetaTagsExceptions, String.join(StrUtils.newLineDelimiter, searchSettings.MetaTagsExceptions));
            updateSearchSettingsPropertyByKey(PropertyKeys.SpecificWordsInDomainURLs, String.join(StrUtils.newLineDelimiter, searchSettings.KeywordsForLookingInDomainURLs));
            updateSearchSettingsPropertyByKey(PropertyKeys.TopLevelDomainsExceptions, String.join(StrUtils.newLineDelimiter, searchSettings.ExceptionsForTopLevelDomains));
            updateSearchSettingsPropertyByKey(PropertyKeys.URLExceptions, String.join(StrUtils.newLineDelimiter, searchSettings.ExceptionsForWordsInDomainURLs));
            updateSearchSettingsPropertyByKey(PropertyKeys.IgnoreSearchTexts, String.join(StrUtils.newLineDelimiter, searchSettings.IgnoreSearchTexts));
            updateSearchSettingsPropertyByKey(PropertyKeys.AcceptedGuessedDomains, String.join(StrUtils.newLineDelimiter, searchSettings.AcceptedGuessedDomainEmails));
        } catch (NullPointerException ex) {
            Logger.tag("SYSTEM").error(ex);
        }
    }

    public SearchSettings getSearchSettings() {
        SearchSettings searchSettings = new SearchSettings();
        try {

            SearchSettingsEntity searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.DomainExceptions);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.ExceptionsForFoundDomains, getSearchSettingsPropertyByKey(PropertyKeys.DomainExceptions).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.KeywordsInSearchResults);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.KeywordsForLookingInSearchResults, getSearchSettingsPropertyByKey(PropertyKeys.KeywordsInSearchResults).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.MetaTagsExceptions);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.MetaTagsExceptions, getSearchSettingsPropertyByKey(PropertyKeys.MetaTagsExceptions).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.SpecificWordsInDomainURLs);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.KeywordsForLookingInDomainURLs, getSearchSettingsPropertyByKey(PropertyKeys.SpecificWordsInDomainURLs).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.TopLevelDomainsExceptions);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.ExceptionsForTopLevelDomains, getSearchSettingsPropertyByKey(PropertyKeys.TopLevelDomainsExceptions).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.URLExceptions);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.ExceptionsForWordsInDomainURLs, getSearchSettingsPropertyByKey(PropertyKeys.URLExceptions).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }

            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.IgnoreSearchTexts);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.IgnoreSearchTexts, getSearchSettingsPropertyByKey(PropertyKeys.IgnoreSearchTexts).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }
            searchSettingsEntity = getSearchSettingsPropertyByKey(PropertyKeys.AcceptedGuessedDomains);
            if (!StringUtils.isEmpty(searchSettingsEntity.getSettingValue())) {
                Collections.addAll(searchSettings.AcceptedGuessedDomainEmails, getSearchSettingsPropertyByKey(PropertyKeys.AcceptedGuessedDomains).getSettingValue()
                        .split(StrUtils.newLineDelimiter));
            }
        } catch (NullPointerException ex) {
            Logger.error(ex);
            Logger.tag("SYSTEM").error(ex);
        }
        return searchSettings;
    }

    public ArrayList<String> getMultipleSearchHeaders() {
        ArrayList<String> headers = new ArrayList<>();
        addNewItemToList(headers, getColumnAProperty());
        addNewItemToList(headers, getColumnBProperty());
        addNewItemToList(headers, getColumnCProperty());
        addNewItemToList(headers, getColumnDProperty());
        addNewItemToList(headers, getColumnEProperty());
        addNewItemToList(headers, getColumnFProperty());
        addNewItemToList(headers, getColumnGProperty());
        addNewItemToList(headers, getColumnHProperty());
        addNewItemToList(headers, getColumnIProperty());
        addNewItemToList(headers, getColumnJProperty());
        addNewItemToList(headers, getColumnKProperty());
        addNewItemToList(headers, getColumnLProperty());
        addNewItemToList(headers, getColumnMProperty());
        addNewItemToList(headers, getColumnNProperty());
        addNewItemToList(headers, getColumnOProperty());
        return headers;
    }

    private void addNewItemToList(ArrayList<String> headers, String item) {
        if (StringUtils.isNotEmpty(item) && headers != null) {
            headers.add(item);
        }
    }

    public boolean getWorkStatus() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.WorkStatus).getSettingValue());
    }

    public synchronized void updateWorkStatus(boolean status) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.WorkStatus, String.valueOf(status));
    }

    public String getSearchPlaceholder() {
        return getApplicationSettingsPropertyByKey(PropertyKeys.SearchPlaceholder).getSettingValue();
    }

    public String getExclusionURLsFileDataPath() {
        return getSearchSettingsPropertyByKey(PropertyKeys.URLsExclusions).getSettingValue();
    }

    public void updateSearchPlaceholder(String searchPlaceholder) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SearchPlaceholder, searchPlaceholder);
    }

    public String getDataFilePath() {
        return getApplicationSettingsPropertyByKey(PropertyKeys.DataFilePath).getSettingValue();
    }

    public synchronized void updateFileDataPath(String filePath) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.DataFilePath, filePath);
    }

    public synchronized void updateExclusionURLsFileDataPath(String filePath) {
        updateSearchSettingsPropertyByKey(PropertyKeys.URLsExclusions, filePath);
    }

    public synchronized boolean getGoogleSearchEngine() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.SearchInGoogleEngine).getSettingValue());
    }

    public synchronized void updateGoogleSearchEngine(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SearchInGoogleEngine, value);
    }

    public synchronized boolean getExportMatchingDomain() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.ExportMatchingDomain).getSettingValue());
    }

    public synchronized void updateExportMatchingDomain(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.ExportMatchingDomain, value);
    }


    public synchronized boolean getGoogleMapsEngine() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.SearchInGoogleMapsEngine).getSettingValue());
    }

    public synchronized void updateGoogleMapsEngine(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SearchInGoogleMapsEngine, value);
    }

    public synchronized void updateSearchPersonNameProperty(boolean value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SearchPersonName, String.valueOf(value));
    }

    public synchronized String getProxyHostProperty() {
        return getApplicationSettingsPropertyByKey(PropertyKeys.ProxyHost).getSettingValue();
    }

    public synchronized void updateProxyHostProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.ProxyHost, value);

    }

    public synchronized String getProxyPortProperty() {
        return getApplicationSettingsPropertyByKey(PropertyKeys.ProxyPort).getSettingValue();
    }

    public synchronized void updateProxyPortProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.ProxyPort, value);

    }

    public synchronized boolean getSearchPersonNameProperty() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.SearchPersonName).getSettingValue());
    }

    public synchronized boolean getCombineSearchColumnsProperty() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.CombineSearchColumns).getSettingValue());
    }

    public synchronized void updateCombineSearchColumnsProperty(boolean value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.CombineSearchColumns, String.valueOf(value));

    }

    public synchronized void updateSearchEmailProperty(boolean value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SearchEmail, String.valueOf(value));

    }

    public synchronized boolean getSearchEmailProperty() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.SearchEmail).getSettingValue());
    }

    public synchronized void updateSearchSingleResultProperty(boolean value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.SingleSearch, String.valueOf(value));
    }

    public synchronized boolean getSearchSingleResultProperty() {
        return Boolean.valueOf(getApplicationSettingsPropertyByKey(PropertyKeys.SingleSearch).getSettingValue());
    }

    /*Columns*/
    public synchronized void updateColumnAProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnA, String.valueOf(value));
    }

    public synchronized String getColumnAProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnA).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnA.toString();
        }
        return value;
    }

    public synchronized void updateColumnBProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnB, String.valueOf(value));
    }

    public synchronized String getColumnBProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnB).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnB.toString();
        }
        return value;
    }

    public synchronized void updateColumnCProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnC, String.valueOf(value));
    }

    public synchronized String getColumnCProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnC).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnC.toString();
        }
        return value;
    }

    public synchronized void updateColumnDProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnD, String.valueOf(value));
    }

    public synchronized String getColumnDProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnD).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnD.toString();
        }
        return value;
    }

    public synchronized void updateColumnEProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnE, String.valueOf(value));
    }

    public synchronized String getColumnEProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnE).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnE.toString();
        }
        return value;
    }

    public synchronized void updateColumnFProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnF, String.valueOf(value));
    }

    public synchronized String getColumnFProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnF).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnF.toString();
        }
        return value;
    }

    public synchronized void updateColumnGProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnG, String.valueOf(value));
    }

    public synchronized String getColumnGProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnG).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnG.toString();
        }
        return value;
    }

    public synchronized void updateColumnHProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnH, String.valueOf(value));
    }

    public synchronized String getColumnHProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnH).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnH.toString();
        }
        return value;
    }

    public synchronized void updateColumnIProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnI, String.valueOf(value));
    }

    public synchronized String getColumnIProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnI).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnI.toString();
        }
        return value;
    }

    public synchronized void updateColumnJProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnJ, String.valueOf(value));
    }

    public synchronized String getColumnJProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnJ).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnJ.toString();
        }
        return value;
    }

    public synchronized void updateColumnKProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnK, String.valueOf(value));
    }

    public synchronized String getColumnKProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnK).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnK.toString();
        }
        return value;
    }

    public synchronized void updateColumnLProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnL, String.valueOf(value));
    }

    public synchronized String getColumnLProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnL).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnL.toString();
        }
        return value;
    }

    public synchronized void updateColumnMProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnM, String.valueOf(value));
    }

    public synchronized String getColumnMProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnM).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnM.toString();
        }
        return value;
    }

    public synchronized void updateColumnNProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnN, String.valueOf(value));
    }

    public synchronized String getColumnNProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnN).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnN.toString();
        }
        return value;
    }

    public synchronized void updateColumnOProperty(String value) {
        updateApplicationSettingsPropertyByKey(PropertyKeys.columnO, String.valueOf(value));
    }

    public synchronized String getColumnOProperty() {
        String value = getApplicationSettingsPropertyByKey(PropertyKeys.columnO).getSettingValue();
        if (StringUtils.isEmpty(value)) {
            value = PropertyKeys.columnO.toString();
        }
        return value;
    }
}
