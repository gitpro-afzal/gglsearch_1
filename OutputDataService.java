package Services;

import Abstract.Models.InputModels.InputCsvModelItem;
import Abstract.Models.OutputModels.IOutputModel;
import Utils.CSVUtils;
import Utils.ConstsStrings;
import Utils.StrUtils;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.lang.StringUtils;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OutputDataService {

    private File outputFile;
    private File notFoundOutputFile;
    private DBConnectionService dbConnectionService;

    public OutputDataService(DBConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    private void createEmptyCSVFile(File outputFile, String[] columns) {
        FileWriter mFileWriter;
        try {
            mFileWriter = new FileWriter(outputFile.getAbsoluteFile());
            CSVWriter mCsvWriter = new CSVWriter(mFileWriter);
            mCsvWriter.writeNext(columns);
            mCsvWriter.close();
            mFileWriter.close();
        } catch (IOException e) {
            Logger.tag("SYSTEM").error(e, "Cannot create empty output multiple searching results file");
        }
    }

    private ArrayList<String> initDynamicHeaders(String placeHolder, List<String> headersFromPreferences) {
        ArrayList<String> headers = new ArrayList<>();
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[a]}")) {
            headers.add(headersFromPreferences.get(0));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[b]}")) {
            headers.add(headersFromPreferences.get(1));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[c]}")) {
            headers.add(headersFromPreferences.get(2));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[d]}")) {
            headers.add(headersFromPreferences.get(3));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[e]}")) {
            headers.add(headersFromPreferences.get(4));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[f]}")) {
            headers.add(headersFromPreferences.get(5));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[g]}")) {
            headers.add(headersFromPreferences.get(6));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[h]}")) {
            headers.add(headersFromPreferences.get(7));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[i]}")) {
            headers.add(headersFromPreferences.get(8));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[j]}")) {
            headers.add(headersFromPreferences.get(9));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[k]}")) {
            headers.add(headersFromPreferences.get(10));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[l]}")) {
            headers.add(headersFromPreferences.get(11));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[m]}")) {
            headers.add(headersFromPreferences.get(12));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[n]}")) {
            headers.add(headersFromPreferences.get(13));
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[o]}")) {
            headers.add(headersFromPreferences.get(14));
        }
        return headers;
    }


    private static String getExportTime() {

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-hha");
        return dateTimeFormatter.format(Calendar.getInstance().getTime());
    }

    private List<String> createFinalHeaders(DBConnectionService dbConnectionService) {
        StrUtils.ResultType resultType = StrUtils.getResultType(dbConnectionService);
        List<String> finalHeaders = new ArrayList<>();
        if (resultType == StrUtils.ResultType.DOMAIN) {
            finalHeaders.add("Page title");
            finalHeaders.add("Website");
            finalHeaders.add("Company address");
            finalHeaders.add("Company phone");
            finalHeaders.add("Not found");
            finalHeaders.add("Broken Link");
        } else {
            String matchingType = resultType == StrUtils.ResultType.INSTAGRAM_FOLLOWER ? "Followers" : "Matching";

            finalHeaders.add("Main title");
            finalHeaders.add(resultType.getSite());
            finalHeaders.add(matchingType);
            finalHeaders.add("Not found");
            finalHeaders.add("Description");
            if (resultType == StrUtils.ResultType.INSTAGRAM_FOLLOWER) {
                finalHeaders.add("Biography");
            }
        }
        return finalHeaders;
    }

    public void createOutputFileForMultipleSearchOutput(String placeHolder, DBConnectionService dbConnectionService) {
        String fileNameFromPlaceHolder = placeHolder.replace("$", "").replace("{", "").replace("}", "").replace("*", "").replace("\"", "");
        outputFile = createFileName(fileNameFromPlaceHolder, true);
        notFoundOutputFile = createFileName(fileNameFromPlaceHolder + "_notfound", true);

        List<String> finalHeaders = createFinalHeaders(dbConnectionService);

        List<String> headers = new ArrayList<>();
        Field[] fields = InputCsvModelItem.class.getDeclaredFields();
        List<Field> sortedFields = Arrays.stream(fields)
                .filter(x -> x.getAnnotation(CsvBindByPosition.class) != null)
                .sorted((Comparator.comparing(Field::getName)))
                .collect(Collectors.toList());
        sortedFields.forEach(field -> {
            try {
                field.setAccessible(true);
                Object header = field.get(InputDataService.csvHeader);
                if (header != null) {
                    headers.add(header.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        headers.addAll(finalHeaders);

        String[] notFoundHeadersArr = headers.toArray(new String[headers.size()]);
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equalsIgnoreCase("Not found")) {
                headers.set(i, "Status");
                break;
            }
        }
        String[] headersArr = headers.toArray(new String[headers.size()]);

        createEmptyCSVFile(getOutputFile(), headersArr);
        createEmptyCSVFile(getNotFoundOutputFile(), notFoundHeadersArr);
    }

    public void createOutputFileForSingleSearchOutput(String placeHolder) {
        String fileNameFromPlaceHolder = placeHolder.replace("$", "").replace("{", "").replace("}", "").replace("*", "").replace("\"", "");
        outputFile = createFileName(fileNameFromPlaceHolder, false);
        if (dbConnectionService.getExportMatchingDomain()) {

            createEmptyCSVFile(getOutputFile(), new String[]{"Page title", "Website", "Company address", "Company phone", "Status", "Broken Link"});
        } else if (placeHolder.contains("site:www.instagram.com") || placeHolder.contains("site:instagram.com")) {
            createEmptyCSVFile(getOutputFile(), new String[]{"Main title", "Instagram", "Followers", "Not found", "Description", "Biography"});
        } else if (placeHolder.contains("site:www.linkedin.com") || placeHolder.contains("site:linkedin.com")) {
            createEmptyCSVFile(getOutputFile(), new String[]{"Main title", "Linkedin", "Roles", "Not found", "Description"});
        } else if (placeHolder.contains("site:www.facebook.com") || placeHolder.contains("site:facebook.com")) {
            createEmptyCSVFile(getOutputFile(), new String[]{"Main title", "Facebook", "Roles", "Not found", "Description"});
        } else {
            createEmptyCSVFile(getOutputFile(), new String[]{"Main title", "Social page", "Information", "Not found", "Description"});

        }
    }

    private File createFileName(String placeHolder, boolean isMultiSearch) {
        StringBuilder inputFileBuilder = new StringBuilder();
        if (isMultiSearch && StringUtils.isNotBlank(dbConnectionService.getDataFilePath())) {
            String inputFile = dbConnectionService.getDataFilePath();
            inputFileBuilder.append(inputFile.substring(0, inputFile.lastIndexOf('.')));
        }
        if (inputFileBuilder.length() > 0) {
            inputFileBuilder.append(" - ");
        }

        String placeSearchHolder = null;
        try {
            placeSearchHolder = StrUtils.replacePlaceholderTermsToData(InputDataService.csvHeader, dbConnectionService.getSearchPlaceholder());
        } catch (Exception ex) {
            Logger.tag("SYSTEM").error("Could not extract column name by search holder " + ex.getMessage(), ex);
        }
        if (StringUtils.isNotBlank(placeSearchHolder)) {
            inputFileBuilder
                    .append(StrUtils.encodeStringToUTF8(placeSearchHolder.trim()))
                    .append(" -- ");
        }

        inputFileBuilder.append(StrUtils.encodeStringToUTF8(placeHolder.trim()))
                .append("_").append(getExportTime()).append(".csv");

        return new File(inputFileBuilder.toString());
    }

    public synchronized void saveResultCsvItemsByMultipleSearch(List<IOutputModel> csvFileData) {
        if (csvFileData == null || csvFileData.size() == 0) {
            return;
        }

        csvFileData = new ArrayList<>(csvFileData);
        List<IOutputModel> foundItems = csvFileData.stream()

                .filter(x -> {
                    String csvContent = x.toCsvRowString();
                    return csvContent.contains(ConstsStrings.RESULT_NOT_MATCHED)
                            || csvContent.contains(ConstsStrings.CONNECTION_ISSUE)
                            || csvContent.contains("Your request failed");
                })
                .collect(Collectors.toList());
        csvFileData.removeAll(foundItems);

        writeToFile(getOutputFile(), csvFileData);
        writeToFile(getNotFoundOutputFile(), foundItems);

    }

    private void writeToFile(File file, List<IOutputModel> outputModels) {
        if (outputModels.size() > 0) {
            try {
                FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);
                for (IOutputModel item : outputModels) {
                    CSVUtils.writeLine(writer, item.toCsvRowString());
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Logger.tag("SYSTEM").error(e, "Cannot save data to output file");
            }
        }
    }

    public void saveResultCsvItems(List<IOutputModel> csvFileData) {
        if (csvFileData == null || csvFileData.size() == 0) {
            Logger.warn("Csv file data empty or null: " + csvFileData);
            return;
        }
        try {
            FileWriter writer = new FileWriter(getOutputFile().getAbsoluteFile(), true);
            for (IOutputModel item : csvFileData) {
                CSVUtils.writeLine(writer, item.toCsvRowString());
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Logger.tag("SYSTEM").error(e, "Cannot save data to output file");
        }
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getNotFoundOutputFile() {
        return notFoundOutputFile;
    }
}
