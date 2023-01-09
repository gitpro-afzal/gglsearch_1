package Abstract.Models.OutputModels;

import Abstract.Models.InputModels.InputCsvModelItem;
import Services.InputDataService;
import Utils.StrUtils;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OutputAdditionalColumnsDataDecorator implements IOutputModel {

    private IOutputModel decorated;
    private InputCsvModelItem inputCsvModelItem;
    private String placeHolder;

    public OutputAdditionalColumnsDataDecorator(IOutputModel outputRegularCSVItem, InputCsvModelItem inputCsvModelItem, String placeHolder) {
        this.decorated = outputRegularCSVItem;
        this.inputCsvModelItem = inputCsvModelItem;
        this.placeHolder = placeHolder;
        if (inputCsvModelItem != null) {
            initAllCsvFields();
        } else {

            initFields();
        }
    }

    @CsvBindByPosition(position = 4)
    private String columnA;

    @CsvBindByPosition(position = 5)
    private String columnB;

    @CsvBindByPosition(position = 6)
    private String columnC;

    @CsvBindByPosition(position = 7)
    private String columnD;

    @CsvBindByPosition(position = 8)
    private String columnE;

    @CsvBindByPosition(position = 9)
    private String columnF;

    @CsvBindByPosition(position = 10)
    private String columnG;

    @CsvBindByPosition(position = 11)
    private String columnH;

    @CsvBindByPosition(position = 12)
    private String columnI;

    @CsvBindByPosition(position = 13)
    private String columnJ;

    @CsvBindByPosition(position = 14)
    private String columnK;

    @CsvBindByPosition(position = 15)
    private String columnL;

    @CsvBindByPosition(position = 16)
    private String columnM;

    @CsvBindByPosition(position = 17)
    private String columnN;

    @CsvBindByPosition(position = 18)
    private String columnO;

    private List<String> data = new ArrayList<>();

    @Override
    public String toCsvRowString() {
        String result = String.join(",",  data.stream().map(x-> StringUtils.isNotBlank(x)?"\"" +x+"\"":x).collect(Collectors.toList()));
        return String.format("%s,%s", result, decorated.toCsvRowString());


    }

    private void initAllCsvFields() {
        if (inputCsvModelItem != null) {
            Field[] fields = InputCsvModelItem.class.getDeclaredFields();
            List<Field> sortedFields = Arrays.stream(fields)
                    .filter(x -> x.getAnnotation(CsvBindByPosition.class) != null)
                    .sorted((Comparator.comparing(Field::getName)))
                    .collect(Collectors.toList());
            InputCsvModelItem csvHeader = InputDataService.csvHeader;
            for (int i = 0; i < sortedFields.size(); i++) {
                Field field = sortedFields.get(i);
                field.setAccessible(true);
                try {
                    Object headerValue = field.get(csvHeader);
                    if (headerValue != null) {
                        Object value = field.get(inputCsvModelItem);
                        if (value != null) {
                            data.add(value.toString());
                        } else {
                            data.add("");
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initFields() {
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[a]}")) {
            setColumnA(inputCsvModelItem.getColumnA());
            data.add(getColumnA());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[b]}")) {
            setColumnB(inputCsvModelItem.getColumnB());
            data.add(getColumnB());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[c]}")) {
            setColumnC(inputCsvModelItem.getColumnC());
            data.add(getColumnC());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[d]}")) {
            setColumnD(inputCsvModelItem.getColumnD());
            data.add(getColumnD());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[e]}")) {
            setColumnE(inputCsvModelItem.getColumnE());
            data.add(getColumnE());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[f]}")) {
            setColumnF(inputCsvModelItem.getColumnF());
            data.add(getColumnF());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[g]}")) {
            setColumnG(inputCsvModelItem.getColumnG());
            data.add(getColumnG());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[h]}")) {
            setColumnH(inputCsvModelItem.getColumnH());
            data.add(getColumnH());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[i]}")) {
            setColumnI(inputCsvModelItem.getColumnI());
            data.add(getColumnI());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[j]}")) {
            setColumnJ(inputCsvModelItem.getColumnJ());
            data.add(getColumnJ());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[k]}")) {
            setColumnK(inputCsvModelItem.getColumnK());
            data.add(getColumnK());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[l]}")) {
            setColumnL(inputCsvModelItem.getColumnL());
            data.add(getColumnL());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[m]}")) {
            setColumnM(inputCsvModelItem.getColumnM());
            data.add(getColumnM());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[n]}")) {
            setColumnN(inputCsvModelItem.getColumnN());
            data.add(getColumnN());
        }
        if (StrUtils.isPlaceholderHasSubstituteTerms(placeHolder, "(?i)\\{column[o]}")) {
            setColumnO(inputCsvModelItem.getColumnO());
            data.add(getColumnO());
        }
    }

    public String getColumnA() {
        return columnA;
    }

    public void setColumnA(String columnA) {
        this.columnA = columnA;
    }

    public String getColumnB() {
        return columnB;
    }

    public void setColumnB(String columnB) {
        this.columnB = columnB;
    }

    public String getColumnC() {
        return columnC;
    }

    public void setColumnC(String columnC) {
        this.columnC = columnC;
    }

    public String getColumnD() {
        return columnD;
    }

    public void setColumnD(String columnD) {
        this.columnD = columnD;
    }

    public String getColumnE() {
        return columnE;
    }

    public void setColumnE(String columnE) {
        this.columnE = columnE;
    }

    public String getColumnF() {
        return columnF;
    }

    public void setColumnF(String columnF) {
        this.columnF = columnF;
    }

    public String getColumnG() {
        return columnG;
    }

    public void setColumnG(String columnG) {
        this.columnG = columnG;
    }

    public String getColumnH() {
        return columnH;
    }

    public void setColumnH(String columnH) {
        this.columnH = columnH;
    }

    public String getColumnI() {
        return columnI;
    }

    public void setColumnI(String columnI) {
        this.columnI = columnI;
    }

    public String getColumnJ() {
        return columnJ;
    }

    public void setColumnJ(String columnJ) {
        this.columnJ = columnJ;
    }

    public String getColumnK() {
        return columnK;
    }

    public void setColumnK(String columnK) {
        this.columnK = columnK;
    }

    public String getColumnL() {
        return columnL;
    }

    public void setColumnL(String columnL) {
        this.columnL = columnL;
    }

    public String getColumnM() {
        return columnM;
    }

    public void setColumnM(String columnM) {
        this.columnM = columnM;
    }

    public String getColumnN() {
        return columnN;
    }

    public void setColumnN(String columnN) {
        this.columnN = columnN;
    }

    public String getColumnO() {
        return columnO;
    }

    public void setColumnO(String columnO) {
        this.columnO = columnO;
    }
}
