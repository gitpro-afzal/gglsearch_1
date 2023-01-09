package Services;

import Abstract.Models.InputModels.InputCsvModelItem;
import Utils.DirUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.tinylog.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InputDataService {
    private static List<InputCsvModelItem> inputCsvModelItems;
    private static File inputDataFile;

    private static List<String> inputUrlsExclusionFileItems;
    private static File inputUrlsExclusionFile;
    public static InputCsvModelItem csvHeader;

    public InputDataService() {
        if (inputCsvModelItems == null) {
            inputCsvModelItems = new ArrayList<>();
        }
        if (inputUrlsExclusionFileItems == null) {
            inputUrlsExclusionFileItems = new ArrayList<>();
        }
    }

    public File getInputDataFile() {
        return inputDataFile;
    }

    public List<InputCsvModelItem> getInputCsvModelItems() {
        return inputCsvModelItems;
    }

    public List<String> getInputUrlsExclusionFileItems() {
        if (inputUrlsExclusionFileItems == null) {
            inputUrlsExclusionFileItems = new ArrayList<>();
        }
        return inputUrlsExclusionFileItems;
    }

    public void initInputFile(File filePath) {
        if (DirUtils.isFileOk(filePath, "csv")) {
            inputDataFile = filePath;
        }
    }

    public void initInputExclusionFile(File filePath) {
        if (DirUtils.isFileOk(filePath, "csv")) {
            inputUrlsExclusionFile = filePath;
        }
    }

    public void initInputExclusionFileData() {
        initInputExclusionFile(inputUrlsExclusionFile);
        if (!DirUtils.isFileOk(inputUrlsExclusionFile, "csv")) {
            return;
        }
        try {
            inputUrlsExclusionFileItems = new ArrayList<>(FileUtils.readLines(inputUrlsExclusionFile, "utf-8"));
        } catch (Exception ex) {
            Logger.tag("SYSTEM").error(ex, "Something wrong with input file");
        }
    }

    public void initInputFileData() {
        initInputFile(getInputDataFile());
        if (!DirUtils.isFileOk(getInputDataFile(), "csv")) {
            return;
        }
        try {

            inputCsvModelItems = readCsv(false);
        } catch (Exception ex) {
            try {
                Logger.tag("SYSTEM").error("Something wrong with input file. Retry with IngoringQuotes");
                inputCsvModelItems = readCsv(true);
            } catch (Exception e) {
                Logger.tag("SYSTEM").error(ex, "Something wrong with input file");
            }
        }
    }

    private List<InputCsvModelItem> readCsv(boolean isIgnoreQuotations) throws IOException {
        try (BufferedReader brHeader = new BufferedReader(new InputStreamReader(new FileInputStream(getInputDataFile().getAbsolutePath()), StandardCharsets.UTF_8));
             BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getInputDataFile().getAbsolutePath()), StandardCharsets.UTF_8))) {
            CSVReader csvReader = new CSVReader(brHeader);
            csvHeader = new InputCsvModelItem();
            String[] headers = csvReader.readNext();
            brHeader.close();
            Field[] fields = InputCsvModelItem.class.getDeclaredFields();
            List<Field> sortedFields = Arrays.stream(fields)
                    .filter(x -> x.getAnnotation(CsvBindByPosition.class) != null)
                    .sorted((Comparator.comparing(Field::getName)))
                    .collect(Collectors.toList());
            for (int i = 0; i < headers.length && i < sortedFields.size(); i++) {
                Field field = sortedFields.get(i);
                field.setAccessible(true);

                field.set(csvHeader, headers[i]);
            }

            CsvToBean csvToBean = new CsvToBeanBuilder(br)
                    .withType(InputCsvModelItem.class)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.NEITHER)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1)
                    .withIgnoreQuotations(isIgnoreQuotations)
                    .build();

            List<InputCsvModelItem> csvInputItems = new ArrayList(IteratorUtils.toList(csvToBean.iterator()));
            // Create empty header for missing header names
            if (!csvInputItems.isEmpty()) {
                InputCsvModelItem defaultItem = csvInputItems.get(0);
                for (Field headerField : sortedFields) {
                    headerField.setAccessible(true);

                    Object headerValue = headerField.get(csvHeader);
                    if (headerValue == null) {

                        Object dataValue = headerField.get(defaultItem);
                        if (dataValue == null) {
                            break;
                        }
                        headerField.set(csvHeader, "");
                    }

                }

            }
            return csvInputItems;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void clearInputDataFile() {
        inputDataFile = null;
    }

    public void clearInputExclsuionURLsDataFile() {
        inputUrlsExclusionFile = null;
    }
}
